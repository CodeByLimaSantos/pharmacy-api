import { describe, it, expect } from 'vitest';
import { cn, formatCurrency, formatDate, formatCPF, formatCNPJ, formatPhone } from '../utils';

describe('cn (className utility)', () => {
  it('should merge class names correctly', () => {
    expect(cn('foo', 'bar')).toBe('foo bar');
  });

  it('should handle conditional classes', () => {
    expect(cn('base', true && 'active', false && 'hidden')).toBe('base active');
  });

  it('should merge Tailwind classes correctly', () => {
    expect(cn('p-4', 'p-2')).toBe('p-2');
  });

  it('should handle undefined and null', () => {
    expect(cn('base', undefined, null, 'end')).toBe('base end');
  });
});

describe('formatCurrency', () => {
  it('should format number as Brazilian currency', () => {
    const result = formatCurrency(1234.56);
    expect(result).toContain('R$');
    expect(result).toContain('1.234,56');
  });

  it('should handle zero', () => {
    const result = formatCurrency(0);
    expect(result).toContain('R$');
    expect(result).toContain('0,00');
  });

  it('should handle negative numbers', () => {
    const result = formatCurrency(-100);
    expect(result).toContain('R$');
    expect(result).toContain('100,00');
  });
});

describe('formatDate', () => {
  it('should format ISO date string', () => {
    const result = formatDate('2024-12-25');
    // Date might vary by timezone, just check format
    expect(result).toMatch(/^\d{2}\/\d{2}\/\d{4}$/);
  });

  it('should format full ISO datetime string', () => {
    const result = formatDate('2024-01-15T10:30:00Z');
    expect(result).toMatch(/^\d{2}\/\d{2}\/\d{4}$/);
  });
});

describe('formatCPF', () => {
  it('should format 11-digit CPF', () => {
    expect(formatCPF('12345678901')).toBe('123.456.789-01');
  });

  it('should return original if not 11 digits', () => {
    expect(formatCPF('1234')).toBe('1234');
    expect(formatCPF('')).toBe('');
  });
});

describe('formatCNPJ', () => {
  it('should format 14-digit CNPJ', () => {
    expect(formatCNPJ('12345678000199')).toBe('12.345.678/0001-99');
  });

  it('should return original if not 14 digits', () => {
    expect(formatCNPJ('1234')).toBe('1234');
    expect(formatCNPJ('')).toBe('');
  });
});

describe('formatPhone', () => {
  it('should format 11-digit phone (mobile)', () => {
    expect(formatPhone('11999887766')).toBe('(11) 99988-7766');
  });

  it('should format 10-digit phone (landline)', () => {
    expect(formatPhone('1133445566')).toBe('(11) 3344-5566');
  });

  it('should return original if invalid length', () => {
    expect(formatPhone('123')).toBe('123');
    expect(formatPhone('')).toBe('');
  });
});
