// Function to handle the quit option in the menu
function handleQuit() {
    // Close the current browser tab
    window.close();
}

// Check if the browser allows closing the window
function setupQuitOption() {
    const quitButton = document.getElementById('quit-menu-item');
    if (quitButton) {
        quitButton.addEventListener('click', handleQuit);
    }
}

setupQuitOption();