package anel;

import java.util.LinkedList;


//Algoritmo de anel construido utilizando uma fila circular.
public class Anel {
	
		static int tamanhoFila;    
	    static LinkedList<Processo> fila;    
	    static LinkedList<Integer> proximoFila;    
	    static int contadorAcessoRegiaoCritica = 0;
	    
	    Anel(int tamanho) { 
	    	fila = new LinkedList<Processo>();
	    	proximoFila = new LinkedList<Integer>();
	    	
	    	this.tamanhoFila = tamanho;
	    	
	    	//põe os processos na fila
	        for (int i = 0; i < tamanhoFila; i++) {
	        	//cria o processo
	            Processo processo = new Processo(i, false);
	            //inicia o processo
	            processo.start();
	            //adiciona o processo na fila
	            fila.add(processo);
	            //define o último processo apontando pro primeiro
	            if(i == tamanhoFila - 1) {
	            	proximoFila.add(i, 0);
	            } else {
	            	proximoFila.add(i, i + 1);   
	            }
	        }
	        
	        //inicia o token no primeiro processo
	        Processo primeiro = fila.element();
	        primeiro.recebeToken(true);
	     }
	    
	    //representação da região crítica
	    private static void regiaoCritica(Processo n) throws Exception {
	    	System.out.println("Processo " + n.indice + " possui Token ? "+ n.possuiToken);
	    	if(n.possuiToken) {
	    		System.out.println("Efetuando ação dentro da região crítica");
	    		Thread.sleep(3500);
	    	} else {
	    		System.err.println("Erro de Acesso");
	    		throw new Exception("Acesso indevido a região crítica");
	    	}
	    	
	    }

		//processo
	    public static class Processo extends Thread {

	        boolean possuiToken;

	        int indice;
	   
	        Processo(int i, boolean t) {
	            this.possuiToken = t;
	            this.indice = i;
	            System.out.println("Processo " + i + " iniciado");
	        }

	        public void recebeToken (boolean t) {
	        	System.out.println("**************************************************");
	        	System.out.println("Processo " + this.indice + " ==> Recebendo o token");
	            this.possuiToken = t;
	            
	            //tenta accessar a região crítica
	            try {
	                acessoRegiaoCritica();
	            } catch (Exception e) {
	                System.err.println("Processo " + this.indice + " ==> " + e.getMessage());
	                System.exit(0);
	            } finally {
	                this.passaToken();
	            }
	        }

	        //passa o token para o próximo processo
	        private void passaToken() {
	            int proximaPosicao = proximoFila.get(indice);
	            System.out.println("Processo " + this.indice + " ==> passando o token para o próximo da fila : " + proximaPosicao);
	            Processo proximoProcesso = fila.get(proximaPosicao);
	            proximoProcesso.recebeToken(this.possuiToken);
	            this.possuiToken = false;
	        }
	        //acesso a região crítica
	        private void acessoRegiaoCritica() throws Exception {
	        	System.out.println("Processo " + this.indice + " ==> Tentando acessar região crítica");
	        	regiaoCritica(this);
			}
			
	        //inicia o processo
	        public void run() {
	        }
	    }
}
