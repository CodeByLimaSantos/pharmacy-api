import { useEffect } from 'react';
import { AppRouter } from './routes';
import { authService } from './services';
import './index.css';

function App() {
  useEffect(() => {
    authService.bootstrapSession();
  }, []);

  return <AppRouter />;
}

export default App;

