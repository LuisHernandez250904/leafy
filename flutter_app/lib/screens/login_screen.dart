import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../providers/auth_provider.dart';
import '../theme/colors.dart';
import '../widgets/leafy_button.dart';

class LoginScreen extends StatefulWidget {
  const LoginScreen({super.key});

  @override
  State<LoginScreen> createState() => _LoginScreenState();
}

class _LoginScreenState extends State<LoginScreen> {
  final _formKey = GlobalKey<FormState>();
  bool _obscure = true;

  @override
  Widget build(BuildContext context) {
    final auth = context.watch<AuthProvider>();

    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(gradient: kGreenGradient),
        child: SafeArea(
          child: Column(
            children: [
              // Header con botón back
              Padding(
                padding: const EdgeInsets.all(16),
                child: Row(
                  children: [
                    GestureDetector(
                      onTap: () => context.pop(),
                      child: Container(
                        padding: const EdgeInsets.all(8),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.2),
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(Icons.arrow_back,
                            color: Colors.white, size: 22),
                      ),
                    ),
                  ],
                ),
              ),
              // Logo
              const Icon(Icons.local_florist,
                  size: 64, color: Colors.white),
              const SizedBox(height: 12),
              Text(
                auth.isLoginMode ? 'Bienvenido de vuelta' : 'Crear cuenta',
                style: const TextStyle(
                  fontSize: 26,
                  fontWeight: FontWeight.w800,
                  color: Colors.white,
                ),
              ),
              Text(
                auth.isLoginMode
                    ? 'Inicia sesión para continuar'
                    : 'Únete a la comunidad Leafy',
                style:
                    TextStyle(color: Colors.white.withOpacity(0.8)),
              ),
              const SizedBox(height: 30),
              // Card del formulario
              Expanded(
                child: Container(
                  width: double.infinity,
                  decoration: BoxDecoration(
                    color: Theme.of(context).scaffoldBackgroundColor,
                    borderRadius: const BorderRadius.vertical(
                        top: Radius.circular(32)),
                  ),
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(28),
                    child: Form(
                      key: _formKey,
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const SizedBox(height: 8),
                          // Nombre (solo en registro)
                          AnimatedSize(
                            duration: const Duration(milliseconds: 250),
                            child: !auth.isLoginMode
                                ? Column(
                                    children: [
                                      _buildLabel('Nombre'),
                                      const SizedBox(height: 6),
                                      TextFormField(
                                        onChanged: auth.setName,
                                        decoration: const InputDecoration(
                                          hintText: 'Tu nombre',
                                          prefixIcon:
                                              Icon(Icons.person_outline),
                                        ),
                                        validator: (v) =>
                                            (v == null || v.isEmpty)
                                                ? 'Ingresa tu nombre'
                                                : null,
                                      ),
                                      const SizedBox(height: 16),
                                    ],
                                  )
                                : const SizedBox.shrink(),
                          ),
                          _buildLabel('Correo electrónico'),
                          const SizedBox(height: 6),
                          TextFormField(
                            onChanged: auth.setEmail,
                            keyboardType: TextInputType.emailAddress,
                            decoration: const InputDecoration(
                              hintText: 'tu@email.com',
                              prefixIcon: Icon(Icons.email_outlined),
                            ),
                            validator: (v) =>
                                (v == null || !v.contains('@'))
                                    ? 'Email inválido'
                                    : null,
                          ),
                          const SizedBox(height: 16),
                          _buildLabel('Contraseña'),
                          const SizedBox(height: 6),
                          TextFormField(
                            onChanged: auth.setPassword,
                            obscureText: _obscure,
                            decoration: InputDecoration(
                              hintText: '••••••••',
                              prefixIcon:
                                  const Icon(Icons.lock_outline),
                              suffixIcon: IconButton(
                                icon: Icon(_obscure
                                    ? Icons.visibility_outlined
                                    : Icons.visibility_off_outlined),
                                onPressed: () =>
                                    setState(() => _obscure = !_obscure),
                              ),
                            ),
                            validator: (v) =>
                                (v == null || v.length < 6)
                                    ? 'Mínimo 6 caracteres'
                                    : null,
                          ),
                          const SizedBox(height: 8),
                          // Error
                          if (auth.errorMessage != null) ...[
                            const SizedBox(height: 8),
                            Container(
                              padding: const EdgeInsets.all(12),
                              decoration: BoxDecoration(
                                color: Colors.red.shade50,
                                borderRadius:
                                    BorderRadius.circular(12),
                                border: Border.all(
                                    color: Colors.red.shade200),
                              ),
                              child: Row(
                                children: [
                                  const Icon(Icons.error_outline,
                                      color: Colors.red, size: 18),
                                  const SizedBox(width: 8),
                                  Expanded(
                                    child: Text(
                                      auth.errorMessage!,
                                      style: const TextStyle(
                                          color: Colors.red,
                                          fontSize: 13),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ],
                          const SizedBox(height: 28),
                          // Botón principal
                          LeafyButton(
                            text: auth.isLoginMode
                                ? 'Iniciar sesión'
                                : 'Registrarse',
                            isLoading: auth.status == AuthStatus.loading,
                            icon: auth.isLoginMode
                                ? Icons.login
                                : Icons.person_add,
                            onPressed: () async {
                              if (_formKey.currentState?.validate() ??
                                  false) {
                                final ok = await auth.submit();
                                if (ok && mounted) {
                                  context.go('/home');
                                }
                              }
                            },
                          ),
                          const SizedBox(height: 16),
                          // Toggle login/registro
                          Center(
                            child: GestureDetector(
                              onTap: auth.toggleMode,
                              child: RichText(
                                text: TextSpan(
                                  style: const TextStyle(fontSize: 14),
                                  children: [
                                    TextSpan(
                                      text: auth.isLoginMode
                                          ? '¿No tienes cuenta? '
                                          : '¿Ya tienes cuenta? ',
                                      style: TextStyle(
                                          color: Colors.grey.shade600),
                                    ),
                                    TextSpan(
                                      text: auth.isLoginMode
                                          ? 'Regístrate'
                                          : 'Inicia sesión',
                                      style: const TextStyle(
                                        color: kPrimary,
                                        fontWeight: FontWeight.w700,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildLabel(String text) => Text(
        text,
        style: const TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 14,
          color: Color(0xFF424242),
        ),
      );
}
