# Finsavior (Backend)

Finsavior é uma aplicação de organização financeira desenvolvida em Java com Spring Boot. Possui implementação de segurança com JWT e uma arquitetura modularizada.

## Descrição + Metas a alcançar

Finsavior é uma ferramenta simples e intuitiva para ajudar no gerenciamento das finanças pessoais. A aplicação permite aos usuários registrar suas receitas, despesas e metas financeiras, fornecendo uma visão clara e detalhada de suas finanças.

## Funcionalidades

- Registro de receitas e despesas
- Categorização de transações financeiras
- Acompanhamento do saldo atual
- Definição de metas financeiras
- Geração de relatórios e gráficos
- Autenticação e autorização de usuários

## Endereço do front

-https://github.com/Hachibitz/finsavior-front.git

## Pré-requisitos

Certifique-se de ter os seguintes requisitos antes de começar:

- Java JDK 8 ou superior
- Maven
- Banco de dados SQL

## Instalação

1. Clone o repositório:

git clone https://github.com/Hachibitz/finsavior-back.git


2. Defina as propriedades de segurança/Configure o banco de dados:

- Crie um banco de dados SQL.
- Configure as informações de conexão no arquivo `application.properties`.

-> Definir as propriedades na VM ou no arquivo ".properties" ou no ".yml": 
* app.jwtExpirationMs={milisseconds}; 
* app.jwtSecret={tokenSecret}; 
* spring.datasource.password={senhaBD};
* spring.datasource.username={usuarioBD}


3. Compile e execute o projeto:

4. Acesse a aplicação em `http://localhost:8080`.

## Configuração Adicional

- Para personalizar as configurações da aplicação, você pode editar o arquivo `application.properties`.
- Para fins de segurança, é altamente recomendável alterar a chave secreta do JWT no arquivo `application.properties`.
- Os endpoints de autenticação e autorização exigem um token JWT válido. Certifique-se de incluir o token de autenticação no cabeçalho `Authorization` das solicitações.

## Contribuição

Contribuições são bem-vindas! Se você quiser contribuir com o projeto, siga as etapas abaixo:

1. Fork o projeto
2. Crie sua branch de recurso (`git checkout -b feature/MyFeature`)
3. Commit suas alterações (`git commit -am 'Add some feature'`)
4. Push para a branch (`git push origin feature/MyFeature`)
5. Abra um Pull Request

## Contato

Se você tiver alguma dúvida ou sugestão, sinta-se à vontade para entrar em contato:

- Autor: [Hachibitz]
- E-mail: [felipealmeida.ns@outlook.com]
- GitHub: [Hachibitz](https://github.com/Hachibitz)