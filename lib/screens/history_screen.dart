import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:provider/provider.dart';
import '../providers/history_provider.dart';
import '../theme/colors.dart';
import '../widgets/bottom_nav_bar.dart';
import '../widgets/history_card.dart';

class HistoryScreen extends StatefulWidget {
  const HistoryScreen({super.key});

  @override
  State<HistoryScreen> createState() => _HistoryScreenState();
}

class _HistoryScreenState extends State<HistoryScreen> {
  String _search = '';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<HistoryProvider>().loadHistory();
    });
  }

  @override
  Widget build(BuildContext context) {
    final prov = context.watch<HistoryProvider>();
    final filtered = prov.histories
        .where((h) =>
            _search.isEmpty ||
            h.plantName.toLowerCase().contains(_search.toLowerCase()) ||
            h.commonName.toLowerCase().contains(_search.toLowerCase()) ||
            h.familyName.toLowerCase().contains(_search.toLowerCase()))
        .toList();

    return Scaffold(
      appBar: AppBar(
        title: const Text('Mi Colección'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.go('/home'),
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => context.read<HistoryProvider>().loadHistory(),
          ),
        ],
      ),
      body: Column(
        children: [
          // Buscador
          Padding(
            padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
            child: TextField(
              onChanged: (v) => setState(() => _search = v),
              decoration: InputDecoration(
                hintText: 'Buscar planta...',
                prefixIcon: const Icon(Icons.search, color: kGray),
                suffixIcon: _search.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear, color: kGray),
                        onPressed: () => setState(() => _search = ''),
                      )
                    : null,
              ),
            ),
          ),
          // Contador
          if (!prov.isLoading)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 4),
              child: Row(
                children: [
                  Text(
                    '${filtered.length} planta${filtered.length != 1 ? 's' : ''}',
                    style: const TextStyle(color: kGray, fontSize: 13),
                  ),
                ],
              ),
            ),
          // Lista
          Expanded(
            child: prov.isLoading
                ? const Center(child: CircularProgressIndicator())
                : prov.error != null && filtered.isEmpty
                    ? _ErrorState(
                        message: prov.error!,
                        onRetry: () =>
                            context.read<HistoryProvider>().loadHistory(),
                      )
                    : filtered.isEmpty
                        ? const _EmptyState()
                        : RefreshIndicator(
                            onRefresh: () =>
                                context.read<HistoryProvider>().loadHistory(),
                            child: ListView.builder(
                              padding:
                                  const EdgeInsets.symmetric(vertical: 8),
                              itemCount: filtered.length,
                              itemBuilder: (_, i) => HistoryCard(
                                history: filtered[i],
                                onTap: () =>
                                    context.go('/detail/${filtered[i].id}'),
                              ),
                            ),
                          ),
          ),
        ],
      ),
      bottomNavigationBar: LeafyBottomNav(
        currentIndex: 1,
        onTap: (i) {
          if (i == 0) context.go('/home');
          if (i == 2) context.go('/explore');
          if (i == 3) context.go('/profile');
        },
      ),
    );
  }
}

class _EmptyState extends StatelessWidget {
  const _EmptyState();

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 90,
            height: 90,
            decoration: BoxDecoration(
              color: const Color(0xFFE8F5E9),
              shape: BoxShape.circle,
            ),
            child: const Icon(Icons.search_off,
                size: 48, color: kPrimaryLight),
          ),
          const SizedBox(height: 16),
          const Text('Sin resultados',
              style:
                  TextStyle(fontSize: 18, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          const Text(
            'Empieza escaneando plantas\ndesde la pantalla de Inicio',
            textAlign: TextAlign.center,
            style: TextStyle(color: kGray),
          ),
        ],
      ),
    );
  }
}

class _ErrorState extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;

  const _ErrorState({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.cloud_off, size: 52, color: kGray),
          const SizedBox(height: 16),
          const Text('Error al cargar',
              style:
                  TextStyle(fontSize: 17, fontWeight: FontWeight.w700)),
          const SizedBox(height: 8),
          Text(message,
              textAlign: TextAlign.center,
              style: const TextStyle(color: kGray, fontSize: 13)),
          const SizedBox(height: 20),
          ElevatedButton.icon(
            onPressed: onRetry,
            icon: const Icon(Icons.refresh),
            label: const Text('Reintentar'),
          ),
        ],
      ),
    );
  }
}
