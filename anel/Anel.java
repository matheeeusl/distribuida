package anel;

import java.util.LinkedList;


//Algoritmo de anel construido utilizando uma fila circular.
public class Anel {
	
		//tamanho da fila
		static int tamanhoFila;    
	    //fila de processos
	    static LinkedList<Processo> fila;    
	    //fila de proximo
	    static LinkedList<Integer> proximoFila;    
	    //contador de acessos a região crítica
	    static int contadorAcessoRegiaoCritica = 0;
	    
	    //construtor
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
	        Processo first = fila.element();
	        first.recebeToken(true);
	     }
	    
	    
	    //representação da região crítica
	    private static void regiaoCritica(Processo n) throws Exception {
	    	System.out.println("Processo "+n.indice+ " possui Token ? "+n.possuiToken);
	    	if(n.possuiToken) {
	    		System.out.println("Efetuando ação dentro da região crítica");
	    		Thread.sleep(3500);
	    	} else {
	    		System.err.println("Erro de Acesso");
	    		throw new Exception("Acesso indevido a região crítica");
	    	}
	    	
	    }

	    /**
	     *     Classe que representa o processo
	     *
	     */
	    public static class Processo extends Thread {

	        boolean possuiToken;

	        int indice;
	   
	        //construtor
	        Processo(int i, boolean t) {
	            this.possuiToken = t;
	            this.indice = i;
	            System.out.println("Processo " + i + " iniciado");
	        }

	        //recebe o token
	        public void recebeToken (boolean t) {
	        	System.out.println("___________________________________________");
	        	System.out.println("Processo " + this.indice + " ==> Recebendo o token");
	            this.possuiToken = t;
	            
	            //tenta accessar a região crítica
	            try {
	                acessoRegiaoCritica();
	            } catch (Exception e) {
	                System.err.println("Processo " + this.indice + " ==> " + e.getMessage());
	                System.exit(0);
	            //libera o token 
	            } finally {
	                this.passaToken();
	            }
	            
	        }


	        //passa o token para o próximo nodo
	        private void passaToken() {
	            int nextPos = proximoFila.get(indice);
	            System.out.println("Processo " + this.indice + " ==> passando o token para o próximo da fila : "+nextPos);
	            Processo next = fila.get(nextPos);
	            next.recebeToken(this.possuiToken);
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
