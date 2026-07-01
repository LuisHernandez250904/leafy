-- ============================================================
-- LEAFY - Migración Supabase
-- Reemplaza la colección "plant_history" de Firestore
-- ============================================================
-- Ejecutar en: Supabase Dashboard > SQL Editor

-- Tabla principal
CREATE TABLE IF NOT EXISTS public.plant_history (
    id          uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id     uuid REFERENCES auth.users(id) ON DELETE CASCADE NOT NULL,
    plant_name  text NOT NULL DEFAULT '',
    common_name text NOT NULL DEFAULT '',
    family_name text NOT NULL DEFAULT '',
    confidence  float8 NOT NULL DEFAULT 0.0,
    description text NOT NULL DEFAULT '',
    image_url   text NOT NULL DEFAULT '',
    scan_date   bigint NOT NULL DEFAULT 0,
    created_at  timestamptz DEFAULT now()
);

-- Índices para consultas eficientes
CREATE INDEX IF NOT EXISTS idx_plant_history_user_id
    ON public.plant_history(user_id);

CREATE INDEX IF NOT EXISTS idx_plant_history_scan_date
    ON public.plant_history(scan_date DESC);

-- ============================================================
-- Row Level Security (RLS) — equivalente a las reglas de Firestore
-- ============================================================
ALTER TABLE public.plant_history ENABLE ROW LEVEL SECURITY;

-- Cada usuario solo puede ver SU propio historial
CREATE POLICY "Users can view own history"
    ON public.plant_history
    FOR SELECT
    USING (auth.uid() = user_id);

-- Cada usuario solo puede insertar con SU user_id
CREATE POLICY "Users can insert own history"
    ON public.plant_history
    FOR INSERT
    WITH CHECK (auth.uid() = user_id);

-- Cada usuario solo puede actualizar sus propios registros
CREATE POLICY "Users can update own history"
    ON public.plant_history
    FOR UPDATE
    USING (auth.uid() = user_id);

-- Cada usuario puede borrar sus propios registros
CREATE POLICY "Users can delete own history"
    ON public.plant_history
    FOR DELETE
    USING (auth.uid() = user_id);

-- ============================================================
-- Storage: bucket para las fotos de plantas
-- (antes se guardaba la ruta local del teléfono, ahora se sube aquí)
-- ============================================================
INSERT INTO storage.buckets (id, name, public)
VALUES ('plant-images', 'plant-images', true)
ON CONFLICT (id) DO NOTHING;

-- Cualquiera puede VER las imágenes (bucket público, son fotos de plantas)
CREATE POLICY "Public read plant images"
    ON storage.objects FOR SELECT
    USING (bucket_id = 'plant-images');

-- Solo usuarios autenticados pueden subir, y solo dentro de su propia carpeta
-- (el código sube a "{user_id}/archivo.jpg")
CREATE POLICY "Users can upload own plant images"
    ON storage.objects FOR INSERT
    WITH CHECK (
        bucket_id = 'plant-images'
        AND auth.uid()::text = (storage.foldername(name))[1]
    );

-- Cada usuario solo puede borrar sus propias imágenes
CREATE POLICY "Users can delete own plant images"
    ON storage.objects FOR DELETE
    USING (
        bucket_id = 'plant-images'
        AND auth.uid()::text = (storage.foldername(name))[1]
    );
