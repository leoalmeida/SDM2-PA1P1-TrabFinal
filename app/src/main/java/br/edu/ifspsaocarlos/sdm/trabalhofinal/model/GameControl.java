package br.edu.ifspsaocarlos.sdm.trabalhofinal.model;


import android.app.Activity;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;

/**
 * Created by LeonardoAlmeida on 18/11/15.
 */
public class GameControl {

    private final   static String TAG = "GameControl";

    private Map<Integer, String>  jogadores = new HashMap<Integer, String>();
    private Map<Integer, Long>  tempoRestante = new HashMap<Integer, Long>();
    private Map<Integer, CountDownTimer>  timers = new HashMap<Integer, CountDownTimer>();
    private int jogadorAtual;
    private final int intervalo = 1000;
    private boolean tempoIniciado = false;
    private TextView txtTempoJogadorAtual, txtTempoJogadorEspera;

    public GameControl(int qtGamers, Long tempoJogo, TextView txttempoJogadorAtual, TextView txttempoJogadorEspera){

        Log.d(TAG, "Inicia Contadores/Tempo de jogo/Nomes dos jogadores");
        for (int i = 0;i < qtGamers;i++){
            timers.put(i, new MyCountDownTimer(tempoJogo * intervalo, intervalo));
            tempoRestante.put(i, tempoJogo * intervalo);
            jogadores.put(i, "Player "+i);
        }

        Log.d(TAG, "Inicia jogo pelo Jogador 1 e guarda os objetos de tempo");
        jogadorAtual = 0;
        this.txtTempoJogadorAtual = txttempoJogadorAtual;
        this.txtTempoJogadorEspera = txttempoJogadorEspera;

        Log.d(TAG, "Atualiza tempo de jogo na Tela");
        this.txtTempoJogadorAtual.setText("" + tempoJogo);
        this.txtTempoJogadorEspera.setText("" + tempoJogo);
    }

    public boolean tick(){
        // Se o jogo ainda não foi iniciado, inicia o jogo
        if (!tempoIniciado) {

            Log.d(TAG, "Marcação de jogo iniciado");
            tempoIniciado = true;

            Log.d(TAG, "inicio de contagem regressiva do jogador 1");
            timers.get(jogadorAtual).start();

            Log.d(TAG, "Jogo Iniciado");
            return true;

        }
        // Senão, apenas troca o contexto do Jogador
        else {

            Log.d(TAG, "Remove temporizador do Jogador atual do contexto");
            timers.get(jogadorAtual).cancel();

            Log.d(TAG, "Troca o contexto do Jogador");
            String tempoJogEspera = txtTempoJogadorAtual.getText().toString();
            txtTempoJogadorEspera.setText(txtTempoJogadorAtual.getText());
            txtTempoJogadorAtual.setText(tempoJogEspera);

            jogadorAtual++;
            if (jogadorAtual>=jogadores.size()) jogadorAtual = 0;

            Log.d(TAG, "Coloca temporizador do Jogador 2 em contexto e inicia a contagem utilizando tempo restante");
            timers.put(jogadorAtual, new MyCountDownTimer(tempoRestante.get(jogadorAtual), intervalo));
            timers.get(jogadorAtual).start();

            return false;

        }
    }

    public class MyCountDownTimer extends CountDownTimer {


        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {

            Log.d(TAG, "Apresenta Mensagem de jogador Vencedor");
            Toast toast = Toast.makeText(txtTempoJogadorAtual.getContext(), jogadores.get(jogadorAtual) + " "+ R.string.ganhou, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();

        }

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "Atualiza na tela o tempo e guarda o tempo atual para ser utilizado na troca de jogadores");
            tempoRestante.put(jogadorAtual, millisUntilFinished);
            txtTempoJogadorAtual.setText("" + millisUntilFinished / 1000);
        }
    }

}
