export const TOKEN_KEY = 'pos_token';
export const TERMINAL_KEY = 'pos_terminal';

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

export function isAuthenticated() {
  return Boolean(getToken());
}

export function setTerminal(code: string) {
  localStorage.setItem(TERMINAL_KEY, code);
}

export function getTerminal() {
  return localStorage.getItem(TERMINAL_KEY);
}
