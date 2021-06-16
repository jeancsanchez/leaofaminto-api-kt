![CI status](https://github.com/jeancsanchez/leaofaminto-api/actions/workflows/gradle.yml/badge.svg) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/060ac6a34b35485b82a67d429cce4d6d)](https://app.codacy.com/gh/jeancsanchez/leaofaminto-api?utm_source=github.com&utm_medium=referral&utm_content=jeancsanchez/leaofaminto-api&utm_campaign=Badge_Grade_Settings)
 
# Leão faminto API
<p align="center">
  <img src="https://user-images.githubusercontent.com/11152015/122151476-02796c80-ce36-11eb-9ee0-dc76e3ed03d6.png">
</p>

API que visa consolidar dados referetes às operações na bolsa para ajudar na declaração de imposto de renda (IRPF).

### Operações suportadas:
- [x] Ações Day trade/Swing trade
- [x] Fundos Imobiliários Day trade/Swing trade
- [ ] Dividendos
- [ ] Juros sobre capital próprio (JCP)
- [ ] Fundos
- [ ] ETFs
- [ ] Stocks (ações americanas)
- [ ] REITs

# Por onde começar?

Primeiramente faça upload do seu extrato de negociação de ativos no portal CEI. Mais informações
aqui: https://riconnect.rico.com.vc/blog/cei

O endpoint para fazer o upload do arquivo é:

```json
POST /api/sync
{
  "arquivo": [Seu arquivo aqui (.xls)]
}
```
