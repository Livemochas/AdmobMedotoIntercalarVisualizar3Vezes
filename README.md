# Metodo Java para visualizar Admob intercalar a cada 3 vezes ou mais

## pode ser colocado em um button ou webview ou em outros

o metodos esta em:

    private void VezesProcessada() {

        // incremente 1 a cada carregamento
        carregamento += 1;
        // funcao para  abrir pagina se carregar mais de 5 vezes
        if (carregamento >= 5) {
            //aqui abre a pagina depois da contagem
            //Toast.makeText(this, "visualizou mais de 3 vezes!", Toast.LENGTH_SHORT).show();

            showInterstitial();
            // aqui zera o cronometro
            carregamento = 0;
        }
    }
