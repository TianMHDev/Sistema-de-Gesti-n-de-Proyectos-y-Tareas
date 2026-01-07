// Set API_URL based on environment
const API_URL = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1'
    ? 'http://localhost:8086/api'
    : 'https://taskflow-backend-i8wn.onrender.com/api';
let authToken = localStorage.getItem('token');
let currentProjectId = null;

// Initialize
document.addEventListener('DOMContentLoaded', () => {
    if (authToken) {
        showView('dashboard-view');
        loadProjects();
    } else {
        showView('auth-view');
    }

    // Tab switching for Auth
    window.switchAuthTab = (tab) => {
        document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
        document.querySelectorAll('.auth-form').forEach(form => form.classList.remove('active'));

        if (tab === 'login') {
            document.querySelector('.tab-btn:first-child').classList.add('active');
            document.getElementById('login-form').classList.add('active');
        } else {
            document.querySelector('.tab-btn:last-child').classList.add('active');
            document.getElementById('register-form').classList.add('active');
        }
    };
});

// View Navigation
function showView(viewId) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    document.getElementById(viewId).classList.add('active');
}

// ==========================================
// Authentication
// ==========================================

document.getElementById('login-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!res.ok) throw new Error('Login failed');

        const data = await res.json();
        authToken = data.token;
        localStorage.setItem('token', authToken);
        showToast('Welcome back!', 'success');
        showView('dashboard-view');
        loadProjects();
    } catch (err) {
        showToast(err.message, 'error');
    }
});

document.getElementById('register-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('reg-username').value;
    const email = document.getElementById('reg-email').value;
    const password = document.getElementById('reg-password').value;

    try {
        const res = await fetch(`${API_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });

        if (!res.ok) throw new Error('Registration failed');

        showToast('Account created! Please log in.', 'success');

        // Reset registration form
        document.getElementById('register-form').reset();

        // Switch to login tab
        switchAuthTab('login');
    } catch (err) {
        showToast(err.message, 'error');
    }
});

function logout() {
    authToken = null;
    localStorage.removeItem('token');
    showView('auth-view');
}

// ==========================================
// Projects
// ==========================================


let allProjects = []; // Estado local para UI optimista

async function loadProjects() {
    const grid = document.getElementById('projects-grid');
    if (grid) {
        // Solo mostrar loading si no tenemos proyectos cargados (evita parpadeo en recargas)
        if (allProjects.length === 0) {
            grid.innerHTML = '<div style="grid-column: 1/-1; text-align: center; color: var(--text-muted); padding: 2rem;"><i class="fas fa-spinner fa-spin fa-2x"></i><p style="margin-top: 10px">Cargando proyectos...</p></div>';
        }
    }

    try {
        const res = await fetch(`${API_URL}/projects`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });
        if (res.status === 401 || res.status === 403) return logout();

        allProjects = await res.json(); // Guardar en estado global
        renderProjects(allProjects);
    } catch (err) {
        console.error(err);
        showToast('Error al cargar proyectos', 'error');
        if (grid && allProjects.length === 0) grid.innerHTML = '<p style="text-align:center; color: var(--danger)">Error al cargar proyectos.</p>';
    }
}

function renderProjects(projects) {
    const grid = document.getElementById('projects-grid');
    if (!grid) return;
    grid.innerHTML = '';

    projects.forEach(project => {
        const card = document.createElement('div');
        card.className = 'project-card glass';

        card.innerHTML = `
            <div class="project-header" onclick="openTaskModal(${JSON.stringify(project).replace(/"/g, '&quot;')})">
                <span class="project-title"><i class="fas fa-folder-open" style="color:var(--primary); margin-right:10px;"></i>${project.name}</span>
                <span class="status-badge status-${project.status}">${project.status}</span>
            </div>
            <div class="project-footer">
                <div class="view-tasks-hint" onclick="openTaskModal(${JSON.stringify(project).replace(/"/g, '&quot;')})">
                    <span><i class="fas fa-tasks"></i> View Tasks</span>
                    <span><i class="fas fa-chevron-right"></i></span>
                </div>
                <div class="project-management-actions">
                    <button class="icon-btn edit-btn" onclick="event.stopPropagation(); showEditProjectModal('${project.id}', '${project.name}')" title="Edit Project"><i class="fas fa-edit"></i></button>
                    <button class="icon-btn delete-btn" onclick="event.stopPropagation(); deleteProject('${project.id}')" title="Delete Project"><i class="fas fa-trash"></i></button>
                </div>
            </div>
        `;
        grid.appendChild(card);
    });
}

// Create/Edit Project
function showCreateProjectModal() {
    document.getElementById('project-modal-title').innerText = 'Create New Project';
    document.getElementById('project-modal-btn').innerHTML = '<i class="fas fa-check"></i> Create Project';
    document.getElementById('edit-project-id').value = '';
    document.getElementById('new-project-name').value = '';
    document.getElementById('create-project-modal').classList.add('active');
}

function showEditProjectModal(id, name) {
    document.getElementById('project-modal-title').innerText = 'Edit Project';
    document.getElementById('project-modal-btn').innerHTML = '<i class="fas fa-save"></i> Save Changes';
    document.getElementById('edit-project-id').value = id;
    document.getElementById('new-project-name').value = name;
    document.getElementById('create-project-modal').classList.add('active');
}

document.getElementById('create-project-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('new-project-name').value;
    const editId = document.getElementById('edit-project-id').value;

    const method = editId ? 'PUT' : 'POST';
    const url = editId ? `${API_URL}/projects/${editId}` : `${API_URL}/projects`;

    // Optimistic UI para Edición (si es nuevo, esperamos respuesta para tener ID)
    const oldProjects = [...allProjects];
    if (editId) {
        allProjects = allProjects.map(p => p.id === editId ? { ...p, name: name } : p);
        renderProjects(allProjects);
        closeModal('create-project-modal');
    }

    try {
        const res = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ name })
        });

        if (!res.ok) throw new Error(`Failed to ${editId ? 'update' : 'create'} project`);

        const savedProject = await res.json();
        
        if (!editId) {
            // Si es creación, agregamos ahora que tenemos el ID real
            allProjects.push(savedProject);
            renderProjects(allProjects);
            closeModal('create-project-modal');
        } else {
            // Actualizamos con la respuesta real del servidor por si acaso
            allProjects = allProjects.map(p => p.id === savedProject.id ? savedProject : p);
            renderProjects(allProjects);
        }

        showToast(`Project ${editId ? 'updated' : 'created'} successfully`, 'success');
        // loadProjects(); // YA NO ES NECESARIO RECARGAR TODO
    } catch (err) {
        // Revertir en caso de error
        if (editId) {
            allProjects = oldProjects;
            renderProjects(allProjects);
        }
        showToast(err.message, 'error');
    }
});

async function deleteProject(projectId) {
    const confirmed = await showConfirm(
        'Delete Project',
        'Are you sure you want to delete this project? All associated tasks will be inaccessible.'
    );

    if (!confirmed) return;

    // ACTUALIZACIÓN OPTIMISTA: Borrar inmediatamente de la pantalla
    const previousProjects = [...allProjects];
    allProjects = allProjects.filter(p => p.id !== projectId);
    renderProjects(allProjects);
    showToast('Project deleted (syncing...)', 'info');

    try {
        const res = await fetch(`${API_URL}/projects/${projectId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!res.ok) throw new Error('Failed to delete project');

        showToast('Project deleted successfully', 'success');
        // loadProjects(); // Ya no es necesario
    } catch (err) {
        // Si falla, revertimos los cambios visuales
        console.error(err);
        allProjects = previousProjects;
        renderProjects(allProjects);
        showToast('Error deleting project', 'error');
    }
}

// ==========================================
// Tasks & Project Details
// ==========================================

async function openTaskModal(project) {
    currentProjectId = project.id;
    document.getElementById('task-modal').classList.add('active');
    document.getElementById('modal-project-title').innerText = project.name;

    // Setup Action Buttons (Activate)
    const actionContainer = document.getElementById('modal-project-actions');
    actionContainer.innerHTML = '';

    if (project.status === 'DRAFT') {
        const btn = document.createElement('button');
        btn.className = 'btn sm-btn activate-btn';
        btn.innerHTML = '<i class="fas fa-rocket"></i> Activate Project';
        btn.onclick = () => activateProject(project.id);
        actionContainer.appendChild(btn);
    }

    loadTasksForProject(project.id);
}


let currentTasks = []; // Estado local para tareas

async function loadTasksForProject(projectId) {
    const list = document.getElementById('modal-task-list');
    
    // Loading state solo si esta vacio para evitar parpadeo
    if (currentTasks.length === 0) {
        list.innerHTML = '<small style="color:var(--text-muted); display:block; text-align:center;">Cargando tareas...</small>';
    }

    try {
        const res = await fetch(`${API_URL}/projects/${projectId}/tasks`, {
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        currentTasks = await res.json();
        renderTasks(currentTasks);
    } catch (err) {
        list.innerText = 'Error loading tasks.';
    }
}

function renderTasks(tasks) {
    const list = document.getElementById('modal-task-list');
    list.innerHTML = tasks.length ? '' : '<div style="text-align:center; padding:2rem; color:var(--text-muted)">No tasks found. Add one below!</div>';

    tasks.forEach(task => {
        const div = document.createElement('div');
        div.className = `task-item ${task.completed ? 'completed' : ''}`;
        div.innerHTML = `
            <div class="task-actions" style="margin-right: 15px;">
                    <button class="check-btn" onclick="completeTask('${task.id}')" title="Mark Complete">
                    <i class="fas fa-check"></i>
                    </button>
            </div>
            <span class="task-title-text">${task.title}</span>
            <div class="task-actions">
                ${!task.completed ? `
                    <button class="icon-btn edit-task-btn" onclick="showEditTaskModal('${task.id}', '${task.title}')" title="Edit Task"><i class="fas fa-edit"></i></button>
                    <button class="icon-btn delete-task-btn" onclick="deleteTask('${task.id}')" title="Delete Task"><i class="fas fa-trash"></i></button>
                ` : ''}
            </div>
        `;
        list.appendChild(div);
    });
}

document.getElementById('add-task-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    if (!currentProjectId) return;

    const input = document.getElementById('new-task-title');
    const title = input.value;

    try {
        const res = await fetch(`${API_URL}/projects/${currentProjectId}/tasks`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ title })
        });

        if (!res.ok) throw new Error('Failed to add task');

        const newTask = await res.json();
        currentTasks.push(newTask);
        renderTasks(currentTasks); // Actualizacion optimista (bueno, casi, esperamos respuesta para ID)
        
        input.value = '';
        showToast('Task added', 'success');
        // loadTasksForProject(currentProjectId); // Ya no necesario
    } catch (err) {
        showToast(err.message, 'error');
    }
});

// Edit/Delete Tasks
function showEditTaskModal(id, title) {
    document.getElementById('edit-task-id').value = id;
    document.getElementById('edit-task-title').value = title;
    document.getElementById('edit-task-modal').classList.add('active');
}

document.getElementById('edit-task-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const id = document.getElementById('edit-task-id').value;
    const title = document.getElementById('edit-task-title').value;

    // Optimistic Update
    const prevTasks = [...currentTasks];
    currentTasks = currentTasks.map(t => t.id === id ? { ...t, title } : t);
    renderTasks(currentTasks);
    closeModal('edit-task-modal');

    try {
        const res = await fetch(`${API_URL}/tasks/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${authToken}`
            },
            body: JSON.stringify({ title })
        });

        if (!res.ok) throw new Error('Failed to update task');
        
        showToast('Task updated', 'success');
        // loadTasksForProject(currentProjectId);
    } catch (err) {
        // Revert
        currentTasks = prevTasks;
        renderTasks(currentTasks);
        showToast(err.message, 'error');
    }
});

async function deleteTask(taskId) {
    const confirmed = await showConfirm(
        'Delete Task',
        'Are you sure you want to delete this task?'
    );

    if (!confirmed) return;

    // Optimistic Update
    const prevTasks = [...currentTasks];
    currentTasks = currentTasks.filter(t => t.id !== taskId);
    renderTasks(currentTasks);

    try {
        const res = await fetch(`${API_URL}/tasks/${taskId}`, {
            method: 'DELETE',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!res.ok) throw new Error('Failed to delete task');

        showToast('Task deleted', 'success');
    } catch (err) {
        currentTasks = prevTasks;
        renderTasks(currentTasks);
        showToast(err.message, 'error');
    }
}

async function completeTask(taskId) {
    // Optimistic Update
    const prevTasks = [...currentTasks];
    currentTasks = currentTasks.map(t => t.id === taskId ? { ...t, completed: true } : t);
    renderTasks(currentTasks);

    try {
        const res = await fetch(`${API_URL}/tasks/${taskId}/complete`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!res.ok) throw new Error('Failed to complete task');

        showToast('Task completed!', 'success');
    } catch (err) {
        currentTasks = prevTasks;
        renderTasks(currentTasks);
        showToast(err.message, 'error');
    }
}

async function activateProject(projectId) {
    try {
        const res = await fetch(`${API_URL}/projects/${projectId}/activate`, {
            method: 'PATCH',
            headers: { 'Authorization': `Bearer ${authToken}` }
        });

        if (!res.ok) {
            const errorText = await res.text();
            try {
                const json = JSON.parse(errorText);
                throw new Error(json.message || json.detail || 'Failed to activate');
            } catch (e) {
                throw new Error('Project needs at least one active task to activate.');
            }
        }

        showToast('Project activated!', 'success');
        closeModal('task-modal');
        loadProjects();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// Utilities
function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

function showConfirm(title, message) {
    return new Promise((resolve) => {
        const modal = document.getElementById('custom-confirm-modal');
        const titleEl = document.getElementById('confirm-title');
        const messageEl = document.getElementById('confirm-message');
        const okBtn = document.getElementById('confirm-ok-btn');
        const cancelBtn = document.getElementById('confirm-cancel-btn');

        titleEl.innerText = title;
        messageEl.innerText = message;
        modal.classList.add('active');

        const handleOk = () => {
            modal.classList.remove('active');
            cleanup();
            resolve(true);
        };

        const handleCancel = () => {
            modal.classList.remove('active');
            cleanup();
            resolve(false);
        };

        const cleanup = () => {
            okBtn.removeEventListener('click', handleOk);
            cancelBtn.removeEventListener('click', handleCancel);
        };

        okBtn.addEventListener('click', handleOk);
        cancelBtn.addEventListener('click', handleCancel);
    });
}

function showToast(msg, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.innerText = msg;
    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Close modals on outside click
window.onclick = function (event) {
    if (event.target.classList.contains('modal')) {
        event.target.classList.remove('active');
    }
}
