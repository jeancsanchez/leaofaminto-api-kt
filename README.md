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