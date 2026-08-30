-- Super Admin único da plataforma (RN-002): conta criada diretamente no banco.
-- Senha provisória de desenvolvimento: SuperAdmin@123 (troque em ambientes reais).
INSERT INTO usuarios (email, senha_hash, perfil, supermercado_id, supermercado_cnpj, ativo)
VALUES (
    'superadmin@sgtm.local',
    '$2a$10$6sGWTFZMjbdUVnebl/HVsuI6zgYm26x0L.rLO2GsqwYRzfxlAtOBm',
    'SUPER_ADMIN',
    NULL,
    NULL,
    TRUE
);
