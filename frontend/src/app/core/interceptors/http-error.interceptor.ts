import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

/**
 * Interceptor para tratamento global de erros HTTP
 */
@Injectable()
export class HttpErrorInterceptor implements HttpInterceptor {
  constructor(private snackBar: MatSnackBar) {}

  intercept(
    request: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        let errorMessage = 'Ocorreu um erro inesperado';
        let statusCode = error.status;

        if (error.error instanceof ErrorEvent) {
          // Erro de client
          errorMessage = error.error.message;
        } else {
          // Erro do servidor
          switch (error.status) {
            case 400:
              errorMessage = error.error?.message || 'Requisição inválida';
              break;
            case 401:
              errorMessage = 'Não autorizado. Faça login novamente.';
              break;
            case 403:
              errorMessage = 'Acesso negado.';
              break;
            case 404:
              errorMessage = 'Recurso não encontrado.';
              break;
            case 409:
              errorMessage = error.error?.message || 'Conflito de dados.';
              break;
            case 500:
              errorMessage = 'Erro interno do servidor.';
              break;
            default:
              errorMessage = `Erro ${statusCode}: ${error.message}`;
          }
        }

        // Mostra notificação para o usuário
        this.snackBar.open(errorMessage, 'Fechar', {
          duration: 5000,
          panelClass: ['error-snackbar']
        });

        return throwError(() => error);
      })
    );
  }
}