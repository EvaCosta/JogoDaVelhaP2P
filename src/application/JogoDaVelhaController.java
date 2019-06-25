package application;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import logic.Jogada;
import logic.Jogo;
import logic.Posicao;

public class JogoDaVelhaController {
	@FXML private TextField txtIP;
	@FXML private Button btnConexao;
	@FXML private Label status;
	@FXML private GridPane tabuleiro;

	private Pane[][] botoes;

	private Jogo jogo;

	@FXML
	public void initialize(){
		Integer aux;

		botoes = new Pane[3][3];
		txtIP.addEventHandler(KeyEvent.KEY_PRESSED, (key)->{
			if(key.getCode() == KeyCode.ENTER)btnConectar();
		});

		for(Node node : tabuleiro.getChildren()){
			if(node instanceof Pane){
				int indexLinha = ((aux = GridPane.getRowIndex(node)) == null) ? 0 : aux;
				int indexColuna = ((aux = GridPane.getColumnIndex(node)) == null) ? 0 : aux;
				botoes[indexLinha][indexColuna] = (Pane)node;
				botoes[indexLinha][indexColuna].addEventHandler(MouseEvent.MOUSE_CLICKED, (event)->clickEvent(event));
			}
		}
	}

	public void btnConectar() {
		if (txtIP.getText().isEmpty())
			return;

		btnConexao.setText("Conectando...");
		btnConexao.setDisable(true);
		txtIP.setDisable(true);

		new Thread(() -> {
		    try {
		    	jogo = new Jogo(txtIP.getText());

				if (!jogo.isServidor()) {
					changeStatus("O oponente comeÃ§a o jogo com " + jogo.tipoJogador.toString(), "");

					new Thread(() -> {
						esperarJogada();
					}).start();
				}
				else {
					changeStatus("VocÃª comeÃ§a o jogo com " + jogo.tipoJogador.toString(), "-fx-background-color: green; -fx-text-fill: white;");
				}

				Platform.runLater(() -> {
					btnConexao.setText("Conectado");
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

		for (int x = 0; x < 3; x++)
			for (int y = 0; y < 3; y++)
				botoes[x][y].setBackground(Background.EMPTY);

		changeStatus("", "");
	}

	private void clickEvent(MouseEvent event) {
		if (jogo != null && jogo.getTurno()) {
			Pane pane = (Pane)event.getSource();
			Integer aux;
			int rowIndex = ((aux = GridPane.getRowIndex(pane)) == null) ? 0 : aux;
			int columnIndex = ((aux = GridPane.getColumnIndex(pane)) == null) ? 0 : aux;


			if (jogo.realizarJogada(rowIndex, columnIndex)) {
				botoes[rowIndex][columnIndex].setBackground(new Background(new BackgroundImage(
						new Image(jogo.tipoJogador.localImagem), BackgroundRepeat.NO_REPEAT,
						BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

				if (!isFimDeJogo()) {
					changeStatus("Vez do oponente | " + Jogada.inverterJogada(jogo.tipoJogador).toString(), "");

					new Thread(() -> {
						esperarJogada();
						isFimDeJogo();
					}).start();
				}
			}
		}
	}

	private boolean isFimDeJogo() {
		Jogada jogada;

		if ((jogada = jogo.checaVitoria()) == null) {
			if (jogo.checaEmpate())
				changeStatus("Fim de jogo: Empate", "");
			else
				return false;
		}
		else {
			if (jogada.equals(jogo.tipoJogador))
				changeStatus("VocÃª venceu!!! :)", "-fx-background-color: green; -fx-text-fill: white;");
			else
				changeStatus("VocÃª perdeu :(", "-fx-background-color: red; -fx-text-fill: white;");
		}

		finalizar();
		return true;
	}

	private void finalizar() {
		jogo.setTurno(false);

		Platform.runLater(() -> {
			btnConexao.setText("Conectar");
			btnConexao.setDisable(false);
			txtIP.setDisable(false);
		});

		jogo.close();
	}

	private void esperarJogada() {
		Posicao pos = jogo.esperarJogada();

		if (pos == null) {
			changeStatus("ConexÃ£o perdida", "-fx-background-color: red; -fx-text-fill: white;");
			finalizar();
		}
		else {
			changeStatus("Sua vez | " + jogo.tipoJogador.toString(), "");

			botoes[pos.getX()][pos.getY()].setBackground(
					new Background(new BackgroundImage(new Image(Jogada.inverterJogada(jogo.tipoJogador).localImagem), BackgroundRepeat.NO_REPEAT,
							BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
		}
	}

	private void changeStatus(String text, String style) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
				status.setText(text);
				status.setStyle(style);
				status.applyCss();
		    }
		});
	}
}
