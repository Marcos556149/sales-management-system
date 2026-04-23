import { useEffect } from 'react';

/**
 * Custom hook for managing global keyboard shortcuts.
 * @param {Object} shortcutMap - Map of shortcut strings to handler functions.
 *   Examples: 
 *   - 'ctrl+p': handler
 *   - 'alt+arrowleft': handler
 *   - 'escape': handler
 */
export const useKeyboardShortcuts = (shortcutMap) => {
  useEffect(() => {
    const handleKeyDown = (event) => {
      // Don't trigger if user is typing in an input field
      const activeElement = document.activeElement;
      const isTyping = 
        activeElement.tagName === 'INPUT' || 
        activeElement.tagName === 'TEXTAREA' || 
        activeElement.isContentEditable;

      if (isTyping) return;

      const key = event.key.toLowerCase();
      const isCtrl = event.ctrlKey || event.metaKey;
      const isAlt = event.altKey;
      const isShift = event.shiftKey;

      // Construct the shortcut string to match against the map
      let shortcutStr = '';
      if (isCtrl) shortcutStr += 'ctrl+';
      if (isShift) shortcutStr += 'shift+';
      if (isAlt) shortcutStr += 'alt+';
      shortcutStr += key;

      // Also check for the key alone (e.g., 'escape')
      if (shortcutMap[shortcutStr]) {
        event.preventDefault();
        shortcutMap[shortcutStr]();
      } else if (shortcutMap[key] && !isCtrl && !isAlt) {
        // Handle single keys like 'escape'
        event.preventDefault();
        shortcutMap[key]();
      }
    };

    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [shortcutMap]);
};
