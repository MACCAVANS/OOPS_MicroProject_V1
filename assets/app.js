// Theme Management
(function initTheme() {
    const savedTheme = localStorage.getItem('forensix-theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
})();

function setTheme(theme) {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('forensix-theme', theme);
}

// Global Custom Modals to replace alert, confirm, prompt
function createAppModalDOM() {
    if (document.getElementById('app-modal')) return;

    const modalHTML = `
        <div class="app-modal-overlay" id="app-modal">
            <div class="app-modal-box">
                <div class="app-modal-title" id="app-modal-title">Notice</div>
                <div class="app-modal-message" id="app-modal-message"></div>
                <input type="text" class="app-modal-input" id="app-modal-input" style="display:none">
                <div class="app-modal-actions">
                    <button class="app-modal-btn app-modal-btn-cancel" id="app-modal-cancel" style="display:none">Cancel</button>
                    <button class="app-modal-btn app-modal-btn-primary" id="app-modal-confirm">OK</button>
                </div>
            </div>
        </div>
    `;
    const div = document.createElement('div');
    div.innerHTML = modalHTML;
    document.body.appendChild(div.firstElementChild);
}

window.appAlert = function(message, title = 'Notice') {
    return new Promise(resolve => {
        createAppModalDOM();
        const modal = document.getElementById('app-modal');
        document.getElementById('app-modal-title').textContent = title;
        document.getElementById('app-modal-message').textContent = message;
        
        document.getElementById('app-modal-input').style.display = 'none';
        document.getElementById('app-modal-cancel').style.display = 'none';
        
        const confirmBtn = document.getElementById('app-modal-confirm');
        confirmBtn.className = 'app-modal-btn app-modal-btn-primary';
        confirmBtn.textContent = 'OK';
        
        modal.classList.add('open');

        confirmBtn.onclick = () => {
            modal.classList.remove('open');
            resolve();
        };
    });
};

window.appConfirm = function(message, title = 'Confirm', isDanger = false) {
    return new Promise(resolve => {
        createAppModalDOM();
        const modal = document.getElementById('app-modal');
        document.getElementById('app-modal-title').textContent = title;
        document.getElementById('app-modal-message').textContent = message;
        
        document.getElementById('app-modal-input').style.display = 'none';
        document.getElementById('app-modal-cancel').style.display = 'block';
        
        const confirmBtn = document.getElementById('app-modal-confirm');
        confirmBtn.className = isDanger ? 'app-modal-btn app-modal-btn-danger' : 'app-modal-btn app-modal-btn-primary';
        confirmBtn.textContent = isDanger ? 'Delete' : 'Confirm';
        
        modal.classList.add('open');

        confirmBtn.onclick = () => {
            modal.classList.remove('open');
            resolve(true);
        };
        
        document.getElementById('app-modal-cancel').onclick = () => {
            modal.classList.remove('open');
            resolve(false);
        };
    });
};

window.appPrompt = function(message, defaultValue = '', title = 'Input Required') {
    return new Promise(resolve => {
        createAppModalDOM();
        const modal = document.getElementById('app-modal');
        document.getElementById('app-modal-title').textContent = title;
        document.getElementById('app-modal-message').textContent = message;
        
        const input = document.getElementById('app-modal-input');
        input.style.display = 'block';
        input.value = defaultValue;
        
        document.getElementById('app-modal-cancel').style.display = 'block';
        
        const confirmBtn = document.getElementById('app-modal-confirm');
        confirmBtn.className = 'app-modal-btn app-modal-btn-primary';
        confirmBtn.textContent = 'Submit';
        
        modal.classList.add('open');
        input.focus();

        confirmBtn.onclick = () => {
            modal.classList.remove('open');
            resolve(input.value);
        };
        
        document.getElementById('app-modal-cancel').onclick = () => {
            modal.classList.remove('open');
            resolve(null);
        };
    });
};
