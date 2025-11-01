import React, { createContext, useContext, ReactNode } from 'react';
import { ToastProvider } from './ToastContext';
import { LoadingProvider } from './LoadingContext';

interface AppContextType {
  // Add global app state here if needed
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  return (
    <AppContext.Provider value={{}}>
      <LoadingProvider>
        <ToastProvider>
          {children}
        </ToastProvider>
      </LoadingProvider>
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) {
    throw new Error('useApp must be used within AppProvider');
  }
  return context;
}

// Re-export context hooks
export { useToast } from './ToastContext';
export { useLoading } from './LoadingContext';

