import java.util.Scanner;

public class FactorizacionLU {

    // ============================================
    // CLASE FRACCIÓN (para manejar enteros, decimales y fracciones exactas)
    // ============================================
    static class Fraction {
        long num; // numerador
        long den; // denominador

        public Fraction(long num, long den) {
            if (den == 0)
                throw new ArithmeticException("Denominador no puede ser cero");
            if (den < 0) {
                num = -num;
                den = -den;
            }
            long g = gcd(Math.abs(num), den);
            this.num = num / g;
            this.den = den / g;
        }
        public Fraction(long num) {
            this(num, 1);
        }

        public Fraction(double value) {
            // Convertir decimal a fracción (precisión limitada)
            this((long) Math.round(value * 1000000), 1000000);
        }

        private static long gcd(long a, long b) {
            while (b != 0) {
                long t = b;
                b = a % b;
                a = t;
            }
            return a;
        }

        public Fraction add(Fraction other) {
            return new Fraction(this.num * other.den + other.num * this.den, this.den * other.den);
        }

        public Fraction subtract(Fraction other) {
            return new Fraction(this.num * other.den - other.num * this.den, this.den * other.den);
        }

        public Fraction multiply(Fraction other) {
            return new Fraction(this.num * other.num, this.den * other.den);
        }

        public Fraction divide(Fraction other) {
            if (other.num == 0)
                throw new ArithmeticException("División por cero");
            return new Fraction(this.num * other.den, this.den * other.num);
        }

        public boolean isZero() {
            return num == 0;
        }

        @Override
        public String toString() {
            if (den == 1)
                return Long.toString(num);
            return num + "/" + den;
        }
    }

    // ============================================
    // FUNCIÓN PARA INGRESAR LOS NÚMEROS
    // ============================================
    static Fraction ingresarNumero(Scanner sc, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String dato = sc.nextLine().trim();

            try {
                if (dato.contains("/")) {
                    String[] partes = dato.split("/");
                    if (partes.length != 2)
                        throw new NumberFormatException();
                    long numerador = Long.parseLong(partes[0].trim());
                    long denominador = Long.parseLong(partes[1].trim());
                    if (denominador == 0) {
                        System.out.println("Error: el denominador no puede ser cero.");
                        continue;
                    }
                    return new Fraction(numerador, denominador);
                } else {
                    // Entero o decimal
                    if (dato.contains(".")) {
                        return new Fraction(Double.parseDouble(dato));
                    } else {
                        return new Fraction(Long.parseLong(dato));
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println();
                System.out.println("Dato incorrecto.");
                System.out.println("Ingrese un número entero, decimal o fracción.");
                System.out.println("Ejemplos: 5, 2.5, 3/4, -7/3");
                System.out.println();
            }
        }
    }

    // ============================================
    // FUNCIÓN PARA MOSTRAR MATRICES
    // ============================================
    static void mostrarMatriz(String nombre, Fraction[][] matriz) {
        System.out.println();
        System.out.println(nombre);
        for (Fraction[] fila : matriz) {
            System.out.print("[ ");
            for (Fraction elemento : fila) {
                System.out.print(elemento + " ");
            }
            System.out.println("]");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("======================================");
        System.out.println("   FACTORIZACIÓN LU DE UNA MATRIZ 3x3");
        System.out.println("           MÉTODO DE DOOLITTLE");
        System.out.println("======================================");
        System.out.println();
        System.out.println("Ejemplos de datos válidos:");
        System.out.println("Entero:     5");
        System.out.println("Decimal:    2.5");
        System.out.println("Fracción:   3/4");
        System.out.println("Fracción:  -5/2");
        System.out.println();

        // ============================================
        // INGRESAR MATRIZ A
        // ============================================
        System.out.println("INGRESE LOS ELEMENTOS DE LA MATRIZ A");
        System.out.println("--------------------------------------");

        Fraction a11 = ingresarNumero(sc, "Ingrese a11: ");
        Fraction a12 = ingresarNumero(sc, "Ingrese a12: ");
        Fraction a13 = ingresarNumero(sc, "Ingrese a13: ");
        Fraction a21 = ingresarNumero(sc, "Ingrese a21: ");
        Fraction a22 = ingresarNumero(sc, "Ingrese a22: ");
        Fraction a23 = ingresarNumero(sc, "Ingrese a23: ");
        Fraction a31 = ingresarNumero(sc, "Ingrese a31: ");
        Fraction a32 = ingresarNumero(sc, "Ingrese a32: ");
        Fraction a33 = ingresarNumero(sc, "Ingrese a33: ");
        // ============================================
        // MATRIZ A
        // ============================================
        Fraction[][] A = {
                { a11, a12, a13 },
                { a21, a22, a23 },
                { a31, a32, a33 }
        };
        // ============================================
        // MATRIZ L (identidad)
        // ============================================
        Fraction[][] L = {
                { new Fraction(1), new Fraction(0), new Fraction(0) },
                { new Fraction(0), new Fraction(1), new Fraction(0) },
                { new Fraction(0), new Fraction(0), new Fraction(1) }
        };

        // ============================================
        // MATRIZ U (ceros)
        // ============================================
        Fraction[][] U = {
                { new Fraction(0), new Fraction(0), new Fraction(0) },
                { new Fraction(0), new Fraction(0), new Fraction(0) },
                { new Fraction(0), new Fraction(0), new Fraction(0) }
        };

        // ============================================
        // FACTORIZACIÓN LU - MÉTODO DE DOOLITTLE
        // ============================================
        // Primera fila de U
        U[0][0] = A[0][0];
        U[0][1] = A[0][1];
        U[0][2] = A[0][2];

        // Verificar primer pivote
        if (U[0][0].isZero()) {
            System.out.println();
            System.out.println("No se puede realizar la factorización LU.");
            System.out.println("El primer pivote es cero.");
        } else {
            // Primera columna de L
            L[1][0] = A[1][0].divide(U[0][0]);
            L[2][0] = A[2][0].divide(U[0][0]);

            // Segunda fila de U
            U[1][1] = A[1][1].subtract(L[1][0].multiply(U[0][1]));
            U[1][2] = A[1][2].subtract(L[1][0].multiply(U[0][2]));

            // Verificar segundo pivote
            if (U[1][1].isZero()) {
                System.out.println();
                System.out.println("No se puede realizar la factorización LU.");
                System.out.println("El segundo pivote es cero.");
            } else {
                // Segunda columna de L
                L[2][1] = (A[2][1].subtract(L[2][0].multiply(U[0][1]))).divide(U[1][1]);

                // Tercera fila de U
                U[2][2] = A[2][2]
                        .subtract(L[2][0].multiply(U[0][2]))
                        .subtract(L[2][1].multiply(U[1][2]));

                // ====================================
                // MOSTRAR RESULTADOS
                // ====================================
                mostrarMatriz("MATRIZ A:", A);
                mostrarMatriz("MATRIZ L:", L);
                mostrarMatriz("MATRIZ U:", U);

                System.out.println();
                System.out.println("======================================");
                System.out.println("FACTORIZACIÓN:");
                System.out.println("A = L × U");
                System.out.println("======================================");
            }
        }

        sc.close();
    }
}