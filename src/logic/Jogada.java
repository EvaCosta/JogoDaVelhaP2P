package logic;

public enum Jogada {
    X("/img/x.png"), O("/img/o.png");

    public String localImagem;

    Jogada(String localImagem) {
        this.localImagem = localImagem;
    }

    public static Jogada inverterJogada(Jogada jogada) {
    	if (jogada == null)
    		return null;

    	if (jogada.equals(X))
    		return O;
    	else
    		return X;
    }
}



