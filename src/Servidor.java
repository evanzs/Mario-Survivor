import java.net.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class Servidor {
  ServerSocket serverSocket = null;

  public static void main(String[] args) {
    new Servidor();
  }
  
  Servidor() {
    final int porto = 48;

    try {
      serverSocket = new ServerSocket(porto);
    } catch (IOException e) {
      System.err.println("O porto " + porto + " n�o pode ser usado.\n" + e);
      
      System.exit(1);
    }

    System.err.println("Servidor esperando cliente...\nDigite <ctrl>+C para terminar.");
      
    while (true) {
      Dados dados = new Dados();
      // Eu usei DataInputstream, mas esta classe � proibida no seu trabalho! Tente usar Scanner
      DataInputStream is[] = new DataInputStream[Dados.NUM_MAX_JOGADORES];
      // Eu usei DataOutputstream, mas esta classe � proibida no seu trabalho! Tente usar PrintStream
      DataOutputStream os[] = new DataOutputStream[Dados.NUM_MAX_JOGADORES];
      
      conectaCliente(Dados.CLIENTE_UM, is, os);
      new Recebendo(Dados.CLIENTE_UM, is, dados).start();
      
      // s� come�a a thread de envio ap�s um cliente chegar
      new Enviando(os, dados).start();
      
      conectaCliente(Dados.CLIENTE_DOIS, is, os);
      new Recebendo(Dados.CLIENTE_DOIS, is, dados).start();
    }
  }
  
  boolean conectaCliente(int id, DataInputStream is[], DataOutputStream os[]) {
    Socket clientSocket = null;
    try {
      clientSocket = serverSocket.accept();

      System.out.println("Cliente " + id + " conectou!");
      
      is[id] = new DataInputStream(clientSocket.getInputStream());
      os[id] = new DataOutputStream(clientSocket.getOutputStream());
      
    } catch (IOException e) {
      System.err.println("N�o foi poss�vel conectar com o cliente.\n" + e);
      
      return false;
    }
    return true;  //funcionou!
  }  
}

/** Esta classe tem os dados dos elementos do jogo, a l�gica e regras 
 * do comportamento destes elementos.
 */
class Dados {

  //DADOS DOS JOGADORES
  static final int NUM_MAX_JOGADORES = 2;
  
  //DADOS DO CLIENTE
  static final int CLIENTE_UM = 0;
  static final int CLIENTE_DOIS = 1;
  static final int LARG_CLIENTE = 1000;
  static final int ALTU_CLIENTE = 750;
  static final int NUM_MAX_PLATAFORMA = 4;
  
  
  class EstadoJogador {
    char c;
    int x, y;
    int dx, dy;
  }
  
  class Plataforma {
	  
	  int x,y;
	  int lx,ly; // largura
  }
  
  //cria jogadores
  EstadoJogador estado[] = new EstadoJogador[NUM_MAX_JOGADORES];
  Plataforma   plataforma[] = new Plataforma[4];
  
  Dados() {
	 
	 estado[0] = new EstadoJogador();
	 estado[0].c = '@';
	 estado[0].x = 30;
	 estado[0].y = 600;
	 estado[0].dx = 3;
	 estado[0].dy = 3;
	 
	 estado[1] = new EstadoJogador();
	 estado[1].c = '@';
	 estado[1].x = 200;
	 estado[1].y = 600;
	 estado[1].dx = 3;
	 estado[1].dy = 3;
	 
	 Random random  = new Random();
	 
		 plataforma[0]= new Plataforma();
		 plataforma[1]= new Plataforma();
		 plataforma[2]= new Plataforma();
		 plataforma[3]= new Plataforma();
		
		 // for? 
		 
		 // top a direita
		 plataforma[1].x = 900;
		 plataforma[1].y = 200;
		 
		 //topo a esquerda
		 plataforma[2].x = 550;
		 plataforma[2].y = 450;
		 
		 
		 plataforma[3].x = 100;
		 plataforma[3].y = 400;
		 
		 plataforma[0].x = 300;
		 plataforma[0].y = 200;
	   
    
  }
  
  /** Envia os dados dos elementos do jogo aos clientes
   */
  
  
  synchronized boolean enviaClientes(DataOutputStream os[]) {
    try {
      // um caracter extra pode ser usado para indicar o tipo de dados
      // est� sendo enviado.
      if (os[CLIENTE_UM] != null) {
        // para enviar ao cliente um inverte o lado do cliente dois
        os[CLIENTE_UM].writeChar(estado[CLIENTE_UM].c);
        os[CLIENTE_UM].writeInt(estado[CLIENTE_UM].x);
        os[CLIENTE_UM].writeInt(estado[CLIENTE_UM].y);
        os[CLIENTE_UM].writeChar(estado[CLIENTE_DOIS].c);
        os[CLIENTE_UM].writeInt(estado[CLIENTE_DOIS].x);
        os[CLIENTE_UM].writeInt(estado[CLIENTE_DOIS].y);
        
         for (int i = 0 ; i < 4; i++)
         {        
          os[CLIENTE_UM].writeInt(plataforma[i].x);
          os[CLIENTE_UM].writeInt(plataforma[i].y);        
         }
      }
      if (os[CLIENTE_DOIS] != null) {
        // para enviar ao cliente dois inverte o lado do cliente um
        os[CLIENTE_DOIS].writeChar(estado[CLIENTE_DOIS].c);
        os[CLIENTE_DOIS].writeInt(estado[CLIENTE_DOIS].x);
        os[CLIENTE_DOIS].writeInt(estado[CLIENTE_DOIS].y);
        os[CLIENTE_DOIS].writeChar(estado[CLIENTE_UM].c);
        os[CLIENTE_DOIS].writeInt(estado[CLIENTE_UM].x);
        os[CLIENTE_DOIS].writeInt(estado[CLIENTE_UM].y);
        
        for (int i = 0 ; i < 4; i++)
        {        
         os[CLIENTE_DOIS].writeInt(plataforma[i].x);
         os[CLIENTE_DOIS].writeInt(plataforma[i].y);        
        }
      
      }
      
      if (os[CLIENTE_UM] != null)
        os[CLIENTE_UM].flush();
      if (os[CLIENTE_DOIS] != null)
        os[CLIENTE_DOIS].flush();
    } catch (IOException ex) {
      System.err.println("O servidor interrompeu a comunica��o.");
       
      return false;
    }
    return true;
  }
  
  synchronized void alteraDados(char c, int id) {
    estado[id].c = c;
  }
  
  synchronized void alteraDados(int x, int y, int id) {
   
    	estado[id].x += x;
    	estado[id].y += y;
    
  }
  
  synchronized void alteraDadosVelocidade(int dx, int dy, int id) {
    estado[id].dx = dx;
    estado[id].dy = dy;
  }
  
  /** Logica do jogo. Os testes das jogadas e das movimenta��es dos 
   * elementos na arena do jogo s�o atualizados aqui.
   */
  synchronized void logicaDoJogo() {
    for (int i = 0; i < NUM_MAX_JOGADORES; i++) {
    
     if ( estado[i].y < 600 )
    	 estado[i].y += estado[i].dx ; 
    
    }
  }
}

/** Esta classe � respons�vel por receber os dados de cada cliente.
 * Uma inst�ncia para cada cliente deve ser executada.
 */
class Recebendo extends Thread {
  DataInputStream is[];
  Dados dados;
  int idCliente;

  Recebendo(int id, DataInputStream is[], Dados d) {
    idCliente = id;
    dados = d;
    this.is = is;
  }
  
  public void run() {
	  
	  
	  
    try {
      while (true) {
        char c = is[idCliente].readChar();
        if (c == 'B')
        {
        	dados.alteraDados(0,20,idCliente);
        }
        if (c == 'C')
        {
        	dados.alteraDados(0,-60,idCliente);
        }
        if (c == 'D')
        {
        	dados.alteraDados(20, 0,idCliente);
        }
        if (c == 'E')
        {
        	dados.alteraDados(-20, 0,idCliente);
        }
        dados.alteraDados(c, idCliente);
      }

    } catch (IOException e) {
      System.err.println("Conexacao terminada pelo cliente");
      
    } catch (NoSuchElementException e) {
      System.err.println("Conexacao terminada pelo cliente");
      
    }
  }
};

/** Esta classe � respons�vel por enciar os dados dos elementos para os 
 * cliente. Uma �nica inst�ncia envia os dados para os dois clientes.
 */
class Enviando extends Thread {
  DataOutputStream os[];
  Dados dados;

  Enviando(DataOutputStream os[], Dados d) {
    dados = d;
    this.os = os;
  }
  
  

  public void run() {
	  
    while (true) {
      dados.logicaDoJogo();
      if (!dados.enviaClientes(os)) {
        break;
      }
      try {
        sleep(33);   // o cliente receber� 30 vezes por segundo
      } catch (InterruptedException ex) {}
    }
  }
};