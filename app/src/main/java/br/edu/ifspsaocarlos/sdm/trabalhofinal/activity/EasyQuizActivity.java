package br.edu.ifspsaocarlos.sdm.trabalhofinal.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import br.edu.ifspsaocarlos.sdm.trabalhofinal.R;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.db.SaveGameDB;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.model.SaveGame;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.dao.SaveGameDAO;
import br.edu.ifspsaocarlos.sdm.trabalhofinal.parser.XMLParser;

public class EasyQuizActivity extends AppCompatActivity {

    TextView textView;
    String resp = ""; // Guarda as respostas erradas
    private SaveGameDAO saveGameDAO =  null;
    private SaveGame saveGame = null;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quizfacil);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Busca o parâmetro da Fase enviada pela Activity anterior
        Intent intent = getIntent();
        saveGameDAO = new SaveGameDAO(getBaseContext());
        saveGame = new SaveGame();
        saveGame.setUser(SaveGameDB.DEFAULT_USER);
        saveGame = saveGameDAO.getSavedGames(saveGame);


        TextView tv = (TextView) findViewById(R.id.tvFase);
        tv.setText("Fase " + new Integer(saveGame.getFase()+1));

        Resources res = getResources();
        // Busca o Asset XML responsável por guardar as informações das fase
        XmlResourceParser resourceParser = res.getXml(R.xml.racas);

        Document doc = null; // Buscando o elemento DOM
        try {
            doc = XMLParser.getXmlFromResource(resourceParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NodeList nl = doc.getElementsByTagName(getString(R.string.KEY_OPTION)); //Busca todos os elementos com a TAG Option
        final int tamanhoBase = nl.getLength();
        Element e = (Element) nl.item(saveGame.getFase()); //Busca o elemento da fase atual

        final String respCerta = XMLParser.getValue(e, getString(R.string.KEY_TITULO)); // Busca no nome da raça da fase atual
        String imgPath = XMLParser.getValue(e, getString(R.string.KEY_IMAGEM)); //Busca o path da imagem


        //Gerando número randomico para a posição da resposta certa
        final Random numRand = new Random();
        final int posRespCerta = numRand.nextInt(4);

        // Busca os Botões de resposta da tela
        Button buttons[] = new Button[4];
        buttons[0] = (Button) findViewById(R.id.btnOpcao1);
        buttons[1] = (Button) findViewById(R.id.btnOpcao2);
        buttons[2] = (Button) findViewById(R.id.btnOpcao3);
        buttons[3] = (Button) findViewById(R.id.btnOpcao4);

        int indicador =0; // Varável utilizada para indicar qual índice deve ser colocado nos botões
        int indice1=saveGame.getFase(),indice2=saveGame.getFase(),indice3=saveGame.getFase(); // Índices com as respostas erradas
        for (int count=0;count<4;count++){
            if (posRespCerta == count){
                resp = respCerta;
                buttons[posRespCerta].setOnClickListener(new Button.OnClickListener(){
                    public void onClick(View v){

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup) findViewById(R.id.toast_layout_root));
                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("Resposta Certa");
                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                        //AIzaSyDJfr15uVfv5dPs4k4oJOW2P13LPMMoXGI
                        //Toast.makeText(getApplicationContext(), "Resposta Correta", Toast.LENGTH_SHORT).show();
                        if (saveGame.getFase()+1 < tamanhoBase){
                            Intent myIntent = new Intent(EasyQuizActivity.this, EasyQuizActivity.class);
                            saveGame.setFase(saveGame.getFase()+1);
                            saveGameDAO.alterar(saveGame);
                            EasyQuizActivity.this.startActivity(myIntent);
                        }else{
                            saveGame.setFase(0);
                            saveGameDAO.alterar(saveGame);
                            Toast.makeText(getApplicationContext(), "Você completou o jogo", Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    }
                });
            }else{
                if (indicador == 0){
                    while (indice1==saveGame.getFase()){indice1 = numRand.nextInt(tamanhoBase);}
                    e = (Element) nl.item(indice1); //Busca o elemento com a resposta 1
                }else if (indicador == 1){
                    indice2 = indice1;
                    while (indice2==indice1 || indice2==saveGame.getFase()){indice2 = numRand.nextInt(tamanhoBase);}
                    e = (Element) nl.item(indice2); //Busca o elemento com a resposta 2
                }else if (indicador == 2){
                    indice3 = indice2;
                    while (indice3==indice2 || indice3 == indice1 || indice3==saveGame.getFase()){indice3 = numRand.nextInt(tamanhoBase);}
                    e = (Element) nl.item(indice3); //Busca o elemento com a resposta 3
                }
                resp = XMLParser.getValue(e, getString(R.string.KEY_TITULO)); // Busca no nome da raça da fase atual
                buttons[count].setText(resp);
                indicador++;
                buttons[count].setOnClickListener(new Button.OnClickListener() {
                    public void onClick(View v) {
                        //Preenche o Texto avisando do erro
                        //Toast.makeText(getApplicationContext(), "Resposta errada! Tente Novamente", Toast.LENGTH_SHORT).show();

                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout,
                                (ViewGroup) findViewById(R.id.toast_layout_root));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("Resposta errada");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                });
            }
            buttons[count].setText(resp);
        }

        //Preenche a Imagem com a Raça da fase
        ImageView iv =(ImageView)findViewById(R.id.ivQuiz);
        iv.setImageURI(Uri.parse(imgPath));

	}


}
