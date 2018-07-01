import java.awt.*;
import java.awt.event.*;

import javax.imageio.ImageIO;
import javax.swing.*;


import java.io.*;
import java.net.*;

class ClienteRede extends JFrame {
  final int porto = 48;
  Image player1,player2;
  Image plataforma1,plataforma2,plataforma3,plataforma4;
  
  Image bg;
  
  
  Desenho des = new Desenho();
  int posX = 0, posY = 0;
  String texto = "";
  int posXAd = 0, posYAd = 0;
  String textoAdversario = "";
  DataOutputStream os = null;
  DataInputStream is = null;
  Socket socket = null;
  
  int platX1,platY1;
  int platX2,platY2;
  int platX3,platY3;
  int platX4,platY4;
  
  
  class Plataforma {
	  int x,y;
  }

  Plataforma   plataforma[] = new Plataforma[4];
 
 
   
  class Desenho extends JPanel {
    Desenho() {
    
    
    	
    	try {
    		player1   = ImageIO.read(new File("run1p1.png"));
    	    player2   = ImageIO.read(new File("run1p2.png"));
            bg   = ImageIO.read(new File("bg.png"));
            
          
          
             plataforma1= ImageIO.read(new File("platG.png"));
             plataforma2= ImageIO.read(new File("platG.png"));
             plataforma3= ImageIO.read(new File("platP.png"));
             plataforma4= ImageIO.read(new File("platP.png"));
            
          } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "A imagem nÃ£o pode ser carregada!\n" + e, "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
          }
    	
    	   
    
      setPreferredSize(new Dimension(1050, 700));
    }

    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      //fundo
      g.drawImage(bg, 0, 0, getSize().width, getSize().height, this);
      
      //você
      g.drawImage(player1, posX,posY, 60,60, this);
      g.drawString(texto, posX, posY);
      
      //adversario
      g.drawImage(player2, posXAd,posYAd, 60,60, this);
      g.drawString(textoAdversario, posXAd, posYAd);
      
      g.drawImage(plataforma1,platX1,platY1,170,32,this);
      g.drawImage(plataforma2,platX2,platY2,170,32,this);
      g.drawImage(plataforma3,platX3,platY3,170,32,this);
      g.drawImage(plataforma4,platX4,platY4,170,32,this);
       
     
      Toolkit.getDefaultToolkit().sync();
    }
  }

  ClienteRede() {
    super("Cliente");
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    add(des);
    
    try {
      socket = new Socket("127.0.0.1",porto);
      // Eu usei DataOutputstream, mas esta classe é proibida no seu 
      // trabalho! Tente usar PrintStream
      os = new DataOutputStream(socket.getOutputStream());
      // Eu usei DataInputstream, mas esta classe é proibida no seu
      // trabalho! Tente usar Scanner
      is = new DataInputStream(socket.getInputStream());
    } catch (UnknownHostException e) {
      // coloque um JOptionPane para mostrar esta mensagem de erro
      System.err.println("Servidor desconhecido.");
      System.exit(1);
    } catch (IOException e) {
      // coloque um JOptionPane para mostrar esta mensagem de erro
      System.err.println("Não pode se conectar ao servidor.");
      System.exit(1);
    }
    
    // Thread que recebe os dados vindos do servidor, prepara as 
    // variáveis de estados dos elementos do jogo e pede o repaint()
    new Thread() {
      public void run() {
        try {
          while (true) {
            // um caracter extra pode ser usado para indicar o tipo de
            // dados está sendo recebido.
        	  
            texto = String.valueOf(is.readChar());
            posX = is.readInt();
            posY = is.readInt();
            
            textoAdversario = String.valueOf(is.readChar());
            posXAd = is.readInt();
            posYAd = is.readInt(); 
            
            // n consigo usar um array aq
            platX1  = is.readInt();
            platY1=  is.readInt();
            platX2  = is.readInt();
            platY2=  is.readInt();
            platX3  = is.readInt();
            platY3=  is.readInt();
            platX4  = is.readInt();
            platY4=  is.readInt();
            repaint();
          }
        } catch (IOException ex) {
          // coloque um JOptionPane para mostrar esta mensagem de erro
          System.err.println("O servidor interrompeu a comunicação");
          
          System.exit(1);
        }
      }
    }.start();
    
    
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        try {
        	 if (e.getKeyCode()==40){
        		 os.writeChar('B');
             }
             //Seta P cima        
             if (e.getKeyCode()==38){
            	 os.writeChar('C');
             }
             //Seta P direita
             if (e.getKeyCode()==39){
            	os.writeChar('D');
             }
             //Seta P/ esquerda
             if (e.getKeyCode()==37){
            	 os.writeChar('E');
             }
           
        
        	
          // apenas a letra está sendo enviada, mas um comando com 
          // coordenadas ou um caracter indicador de mudança de
          // comportamento do jogador poderia ser enviado dependendo da
          // dinâmica do jogo

        } catch (IOException ex) {
          // coloque um JOptionPane para mostrar esta mensagem de erro
          System.err.println("O servidor interrompeu a comunicação");
          
          System.exit(1);
        }
        
      }
    });
    
    pack();
    setVisible(true);
  }

  static public void main(String[] args) {
    ClienteRede f = new ClienteRede();
  }
}
