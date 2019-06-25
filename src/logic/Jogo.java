package logic;

import java.io.IOException;

import util.SocketClienteServidor;

public class Jogo {
	private Jogada tabuleiro[][];
	public Jogada tipoJogador;

	private SocketClienteServidor clienteServidor;

	public Boolean turno = true;

	public Jogo(String ip) throws IOException {
		tabuleiro = new Jogada[3][3];
		clienteServidor = new SocketClienteServidor(ip);
		
		if (clienteServidor.isServidor())
			tipoJogador  = Jogada.X;
		else
			tipoJogador = Jogada.O;
	}
	
	public boolean isServidor() {
		return clienteServidor.isServidor();
	}

	public boolean realizarJogada(int x, int y) {

		if (tabuleiro[x][y] == null) {
			try {
				tabuleiro[x][y] = tipoJogador;
				clienteServidor.send(new Posicao(x,y));

			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}

		return false;
	}

	public Posicao esperarJogada(){
		setTurno(false);

		Posicao pos = clienteServidor.receive();

		if (pos == null)
			return null;
		
		setTurno(true);

		tabuleiro[pos.getX()][pos.getY()] = Jogada.inverterJogada(tipoJogador);
		return pos;
	}

	public Jogada checaVitoria() {
		Jogada jogada;
		
		if ((jogada = checaColunaOuLinha(true)) != null)
			return jogada;

		if ((jogada = checaColunaOuLinha(false)) != null)
			return jogada;

		if ((jogada = checaDiagonais()) != null)
			return jogada;

		return null;
	}
	
	public boolean checaEmpate() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (tabuleiro[i][j] == null)
					return false;
		
		return true;
	}

	public Jogada checaColunaOuLinha(boolean colunaOuLinha) {
		Jogada jogada;
		int y;

		for (int x = 0; x < 3; x++) {
			if (colunaOuLinha)
				jogada = tabuleiro[0][x];
			else
				jogada = tabuleiro[x][0];

			if (jogada != null) {
				for (y = 1; y < 3; y++)
					if ((colunaOuLinha && jogada != tabuleiro[y][x]) || (!colunaOuLinha && jogada != tabuleiro[x][y]))
						break;

				if (y == 3)
					return jogada;
			}
		}

		return null;
	}

	public Jogada checaDiagonais() {
		Jogada jogada;
		
		jogada = tabuleiro[0][0];

		if (jogada != null && tabuleiro[1][1] == jogada && tabuleiro[2][2] == jogada)
			return jogada;

		jogada = tabuleiro[0][2];

		if (jogada != null && tabuleiro[1][1] == jogada && tabuleiro[2][0] == jogada)
			return jogada;

		return null;
	}

	public void setTurno(Boolean turno) {
		synchronized (this.turno) {
			this.turno = turno;
		}
	}
	public synchronized Boolean getTurno() {
		synchronized (this.turno) {
			return turno;
		}
	}
	
	public void close() {
		try {
			clienteServidor.close();
			
		} catch (IOException e) {
		}
	}
}
