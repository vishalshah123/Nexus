const $ = (id) => document.getElementById(id);
const state = { userId: localStorage.getItem("nexus.userId") || "", conversationId: "", conversations: [], messages: [] };
const api = async (path, options = {}) => {
  const response = await fetch(path, { headers: { "Content-Type": "application/json", ...(options.headers || {}) }, ...options });
  if (response.status === 204) return null;
  const raw = await response.text();
  let data; try { data = raw ? JSON.parse(raw) : null; } catch { data = raw; }
  if (!response.ok) throw new Error(data?.message || data?.detail || data?.error || raw || `HTTP ${response.status}`);
  return data;
};
const toast = (message) => { const el=$("toast"); el.textContent=message; el.classList.add("show"); setTimeout(()=>el.classList.remove("show"),3000); };
const esc = (value) => String(value ?? "").replace(/[&<>"']/g, c => ({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
const activeUser = () => $("userSelect").selectedOptions[0]?.textContent || "Local workspace";
function setStatus(ok, text) { $("statusDot").classList.toggle("online",ok); $("apiStatus").textContent=text; }
async function health() { try { const h=await api("/actuator/health"); setStatus(h.status==="UP","API online"); } catch { setStatus(false,"API unavailable"); } }
function switchView(view) {
  document.querySelectorAll(".nav-item").forEach(b=>b.classList.toggle("active",b.dataset.view===view));
  document.querySelectorAll(".view").forEach(v=>v.classList.toggle("active",v.id===view+"View"));
  $("pageTitle").textContent={chat:"AI Workspace",memory:"Long-term Memory",tools:"Tools & Tasks"}[view];
  if(view==="memory") loadMemories(); if(view==="tools"){loadTools();loadTasks();}
}
async function loadUsers() {
  const users=await api("/api/users");
  $("userSelect").innerHTML='<option value="">Select a user…</option>'+users.map(u=>`<option value="${esc(u.id)}">${esc(u.displayName || u.email)} · ${esc(u.email)}</option>`).join("");
  if(state.userId && users.some(u=>u.id===state.userId)) $("userSelect").value=state.userId;
  else if(users.length){state.userId=users[0].id;$("userSelect").value=state.userId;localStorage.setItem("nexus.userId",state.userId);}
  $("sidebarUser").textContent=state.userId?activeUser():"Local workspace";
  if(state.userId) await loadConversations(); else $("conversationList").innerHTML='<div class="empty-state">Create a user to begin.</div>';
}
async function createUser() {
  const displayName=$("newUserName").value.trim(), email=$("newUserEmail").value.trim();
  if(!email){toast("Enter a valid email address.");return;}
  const user=await api("/api/users",{method:"POST",body:JSON.stringify({email,displayName})});
  state.userId=user.id;localStorage.setItem("nexus.userId",state.userId);
  $("newUserName").value="";$("newUserEmail").value="";await loadUsers();$("userSelect").value=state.userId;toast("User created.");
}
async function loadConversations() {
  state.conversations=await api("/api/conversations?userId="+encodeURIComponent(state.userId));
  $("conversationList").innerHTML=state.conversations.length?state.conversations.map(c=>`<div class="conversation-item ${c.id===state.conversationId?"active":""}" data-id="${esc(c.id)}"><b>${esc(c.title||"New conversation")}</b><small>${esc(c.provider||"default")} · ${new Date(c.updatedAt||c.createdAt).toLocaleString()}</small></div>`).join(""):'<div class="empty-state">No conversations yet.<br>Create one to start chatting.</div>';
  document.querySelectorAll(".conversation-item").forEach(el=>el.addEventListener("click",()=>selectConversation(el.dataset.id)));
}
async function newConversation() {
  if(!state.userId){toast("Create or select a user first.");return;}
  const c=await api("/api/conversations",{method:"POST",body:JSON.stringify({userId:state.userId,title:"New conversation",provider:"openai"})});
  state.conversationId=c.id;await loadConversations();await loadMessages();setComposer(true);$("chatTitle").textContent=c.title;$("chatSubtitle").textContent="Conversation saved in PostgreSQL";
}
async function selectConversation(id) { state.conversationId=id;await loadConversations();await loadMessages();setComposer(true);const c=state.conversations.find(x=>x.id===id);$("chatTitle").textContent=c?.title||"Conversation";$("chatSubtitle").textContent="Conversation saved in PostgreSQL"; }
function setComposer(enabled){$("messageInput").disabled=!enabled;$("sendBtn").disabled=!enabled;if(enabled)$("messageInput").focus();}
async function loadMessages() {
  if(!state.conversationId){$("messages").innerHTML='<div class="empty-state">Select or create a conversation.</div>';return;}
  state.messages=await api("/api/conversations/"+state.conversationId+"/messages");
  renderMessages();
}
function renderMessages() {
  if(!state.messages.length){$("messages").innerHTML='<div class="empty-state">No messages yet. Say hello to NEXUS.</div>';return;}
  $("messages").innerHTML=state.messages.map(m=>`<div class="message ${m.role==="user"?"user":"assistant"}"><div class="message-meta">${esc(m.role.toUpperCase())} · ${new Date(m.createdAt).toLocaleTimeString()}</div><div class="message-body">${esc(m.content)}</div></div>`).join("");
  $("messages").scrollTop=$("messages").scrollHeight;
}
async function sendMessage(message) {
  if(!state.conversationId||!message.trim())return;
  $("messageInput").value="";$("sendBtn").disabled=true;
  const pending=document.createElement("div");pending.className="message assistant";pending.innerHTML='<div class="message-meta">NEXUS</div><div class="message-body">Thinking…</div>';$("messages").appendChild(pending);$("messages").scrollTop=$("messages").scrollHeight;
  try { await api("/api/conversations/"+state.conversationId+"/chat",{method:"POST",body:JSON.stringify({message:message.trim()})});await loadMessages();await loadConversations(); }
  catch(e){pending.remove();toast(e.message);}
  finally{$("sendBtn").disabled=false;$("messageInput").focus();}
}
async function loadMemories() {
  if(!state.userId){$("memoryList").innerHTML='<div class="empty-state">Create or select a user first.</div>';return;}
  const memories=await api("/api/v1/users/"+state.userId+"/memories");
  $("memoryList").innerHTML=memories.length?memories.map(m=>`<article class="memory-card"><button data-delete-memory="${esc(m.id)}">Delete</button><p>${esc(m.content)}</p><small>${esc(m.category||"GENERAL")} · IMPORTANCE ${esc(m.importance)} · ${new Date(m.createdAt).toLocaleString()}</small></article>`).join(""):'<div class="empty-state">No memories stored for this user.</div>';
  document.querySelectorAll("[data-delete-memory]").forEach(b=>b.addEventListener("click",async()=>{try{await api("/api/v1/users/"+state.userId+"/memories/"+b.dataset.deleteMemory,{method:"DELETE"});await loadMemories();toast("Memory deleted.");}catch(e){toast(e.message);}}));
}
async function loadTools(){try{const tools=await api("/api/v1/tools");$("toolList").innerHTML=tools.map(t=>`<div class="tool-row"><b>${esc(t.name)}</b><p>${esc(t.description)} · ${esc(t.risk)}</p></div>`).join("");}catch(e){$("toolList").innerHTML='<div class="empty-state">'+esc(e.message)+'</div>';}}
async function runTool(){if(!state.userId){toast("Select a user first.");return;}const toolName=$("toolChoice").value;const arguments_=toolName==="nexus.echo"?{text:$("toolArgument").value}:{};try{const out=await api("/api/v1/tools/execute",{method:"POST",headers:{"X-Nexus-User-Id":state.userId},body:JSON.stringify({toolName,arguments:arguments_})});$("toolOutput").textContent=JSON.stringify(out,null,2);}catch(e){$("toolOutput").textContent=e.message;}}
async function loadTasks(){if(!state.userId){$("taskList").innerHTML='<div class="empty-state">Select a user first.</div>';return;}try{const tasks=await api("/api/v1/users/"+state.userId+"/tasks");$("taskList").innerHTML=tasks.length?tasks.map(t=>`<div class="task-row"><b>${esc(t.taskType)} · ${esc(t.status)}</b><p>${esc(t.id)} · ${new Date(t.createdAt).toLocaleString()}</p></div>`).join(""):'<div class="empty-state">No tasks queued yet.</div>';}catch(e){$("taskList").innerHTML='<div class="empty-state">'+esc(e.message)+'</div>';}}
async function queueTask(){if(!state.userId){toast("Select a user first.");return;}try{await api("/api/v1/users/"+state.userId+"/tasks",{method:"POST",body:JSON.stringify({taskType:$("taskChoice").value,payload:{source:"nexus-console"}})});await loadTasks();toast("Task queued. A worker is not yet configured.");}catch(e){toast(e.message);}}
document.querySelectorAll(".nav-item").forEach(b=>b.addEventListener("click",()=>switchView(b.dataset.view)));
$("userSelect").addEventListener("change",async e=>{state.userId=e.target.value;state.conversationId="";localStorage.setItem("nexus.userId",state.userId);$("sidebarUser").textContent=state.userId?activeUser():"Local workspace";setComposer(false);await loadConversations();await loadMessages();});
$("createUserBtn").addEventListener("click",()=>createUser().catch(e=>toast(e.message)));
$("newConversationBtn").addEventListener("click",()=>newConversation().catch(e=>toast(e.message)));
$("chatForm").addEventListener("submit",e=>{e.preventDefault();sendMessage($("messageInput").value);});
$("messageInput").addEventListener("keydown",e=>{if(e.key==="Enter"&&!e.shiftKey){e.preventDefault();$("chatForm").requestSubmit();}});
document.querySelectorAll("[data-prompt]").forEach(b=>b.addEventListener("click",()=>{$("messageInput").value=b.dataset.prompt;$("messageInput").focus();}));
$("memoryForm").addEventListener("submit",async e=>{e.preventDefault();if(!state.userId){toast("Select a user first.");return;}try{await api("/api/v1/users/"+state.userId+"/memories",{method:"POST",body:JSON.stringify({content:$("memoryContent").value.trim(),category:$("memoryCategory").value.trim()||"GENERAL",source:$("memorySource").value.trim()||"nexus-console",importance:3,tags:[]})});$("memoryContent").value="";await loadMemories();toast("Memory saved.");}catch(err){toast(err.message);}});
$("loadMemoriesBtn").addEventListener("click",()=>loadMemories().catch(e=>toast(e.message)));
$("runToolBtn").addEventListener("click",runTool);
$("loadTasksBtn").addEventListener("click",()=>loadTasks().catch(e=>toast(e.message)));
$("queueTaskBtn").addEventListener("click",queueTask);
$("refreshBtn").addEventListener("click",async()=>{await health();try{await loadUsers();toast("Workspace refreshed.");}catch(e){toast(e.message);}});
(async()=>{await health();try{await loadUsers();}catch(e){setStatus(false,"API unavailable");toast(e.message);}})();