![CI status](https://github.com/jeancsanchez/leaofaminto-api/actions/workflows/gradle.yml/badge.svg?branch=dev) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/060ac6a34b35485b82a67d429cce4d6d)](https://app.codacy.com/gh/jeancsanchez/leaofaminto-api?utm_source=github.com&utm_medium=referral&utm_content=jeancsanchez/leaofaminto-api&utm_campaign=Badge_Grade_Settings)
 [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/5e811cac366549ab9f6c594e26318387)](https://www.codacy.com/gh/jeancsanchez/leaofaminto-api/dashboard?utm_source=github.com&utm_medium=referral&utm_content=jeancsanchez/leaofaminto-api&utm_campaign=Badge_Coverage)
 
# Leão Faminto API
API que visa consolidar dados referentes às operações na bolsa para ajudar na declaração de imposto de renda (IRPF).
<p align="center">
  <img src="https://user-images.githubusercontent.com/11152015/122151476-02796c80-ce36-11eb-9ee0-dc76e3ed03d6.png">
</p>


1. [Motivação](#motivação)
2. [Features prontas ou desejáveis](#features-prontas-ou-desejáveis)
3. [Corretoras suportadas](#corretoras-suportadas)
4. [Integrações prontas](#integrações-prontas)
5. [Importando arquivo do CEI](#importando-arquivo-do-cei)
6. [Aviso legal](#aviso-legal)
<br/>


### Motivação:
Todo o ano o investidor tem que fazer a declaração anual de imposto de renda.
Lá, ele precisa declarar as suas ações, lucros e dividendos. O investidor também precisa pagar imposto todo o mês que obtiver lucro (em certas operações) e ele fica responsável por fazer o cálculo do imposto, gerar o boleto (Darf) e pagar.
O que geralmente acontece é que todo o ano o investidor tem que correr atrás nas corretoras de baixar as notas de corretagem ou pegar essas informações de planilhas próprias e coloca-las manualmente no programa da Receita Federal.
Existem no mercado plataformas como Bastter e Trade Map que consolida todas essas informações, porém são plataformas pagas. A ideia do **Leão Faminto API** é fazer um sistema open-source para que todo investidor possa ter acesso a essas informações mínimas gratuitamente.


### Features prontas ou desejáveis:
- [x] Impostos do mês com ações Day trade/Swing trade
- [x] Impostos do mês com Fundos Imobiliários Day trade/Swing trade
- [ ] Impostos do mês com Stocks Day trade/Swing trade (EUA)
- [ ] Impostos do mês com REITs Day trade/Swing trade (EUA)
- [x] Relatório dos ativos consolidados
- [ ] Relatório geral auxiliar para a declaração de imposto de renda anual.

### Corretoras suportadas:
- [x] Clear
- [ ] Inter
- [ ] XP
- [ ] Easy Invest
- [ ] Rico
- [ ] BTG
- [ ] Toro
- [ ] Passfolio
- [ ] Avenue

### Integrações prontas:
- [x] Portal do Investidor CEI (arquivo .xls)
- [ ] B3 api
- [ ] Relatório das corretoras suportadas (?)

### Importando arquivo do CEI

Primeiramente faça upload do seu extrato de negociação de ativos no portal CEI. Mais informações
aqui: https://riconnect.rico.com.vc/blog/cei

O endpoint para fazer o upload do arquivo é:

```json
POST /api/sync
{
  "arquivo": [Seu arquivo aqui (.xls)]
}
```

### Aviso legal
Esse projeto não indica ou faz recomendações de compras.
Todos os exemplos utilizados são meros objetos de teste.

Os autores desse repositório também não se responsabilizam por eventuais perdas ou ganhos financeiros de qualquer espécie. 
