import { contextBridge, ipcRenderer } from 'electron';

contextBridge.exposeInMainWorld('pos', {
  printRaw: (payload: string) => ipcRenderer.invoke('print-raw', payload)
});
