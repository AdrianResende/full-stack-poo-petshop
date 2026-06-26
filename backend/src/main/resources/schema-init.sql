-- Script de criacao inicial do banco de dados
-- Execute este script no PostgreSQL antes de rodar a aplicacao

CREATE DATABASE petshop_db;

\c petshop_db;

-- As tabelas sao criadas automaticamente pelo Hibernate (spring.jpa.hibernate.ddl-auto=update)
-- Este arquivo serve apenas para criar o banco de dados

-- Dados de exemplo para teste (opcional):
-- INSERT INTO proprietarios (nome, endereco, telefone, email) VALUES
--   ('João Silva', 'Rua das Flores, 123 - Centro', '(32) 99999-0001', 'joao@email.com'),
--   ('Maria Souza', 'Av. Principal, 456 - Bairro Novo', '(32) 99999-0002', 'maria@email.com');
