package Classes;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.lang.Math;
import java.nio.ByteBuffer;


public class Huffman {
	

	public Map<Character, String> charToCode;
	public Map<String, Character> codeToChar;
	public Map<Character, Integer> charToInt;
	public Node rootTrie;
	public String fileText;
	public String code;
	public String preOrderCode;
	
	public Huffman(String text) {
		this.charToCode = new HashMap<Character, String>();
		this.codeToChar = new HashMap<String, Character>();
		this.charToInt = new HashMap<Character, Integer>();
		this.rootTrie = null;
		this.fileText = text;
		this.code = new String();
		this.preOrderCode = new String();
	}

	public static void main(String[] args) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(new File("text.txt")));
		
		String text = in.readLine();
		
		Huffman huff = new Huffman(text);

		huff.setMapCharToInt();
		huff.buildTrie();
		huff.setMaps();
		
		//Imprimir o codigo (0s e 1s) de cada caractere
		for(Map.Entry<Character, String> entry : huff.charToCode.entrySet()) {
			System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
		}
		
		System.out.println("");
		System.out.println("");
		huff.print();
		System.out.println("");
		

		//System.out.println("Char c: " + huff.getCodeFromChar('c'));
		//System.out.println("Char e: " + huff.getCodeFromChar('e'));
		
		huff.concatenateCode();
		huff.setPreOrderCode();
		huff.writeFile();
		
		
		System.out.println("preOrder code: " + huff.preOrderCode);
		System.out.println("");
		System.out.println("Descompressing...");
		huff.descompress();
		System.out.println("");
		in.close();
	}

	
	
	public void setMapCharToInt() {
		for(int i = 0; i < this.fileText.length(); i++) {
			char caracter =  this.fileText.charAt(i);
			if(this.charToInt.containsKey(caracter)) {
				int value = this.charToInt.remove(caracter);
				this.charToInt.put(caracter, value+1);
			}
			else this.charToInt.put(caracter,1);
		}
		
	}
	
	public void buildTrie() {
		this.rootTrie = buildT();
	}
	
	
	private Node buildT() {
		
		PriorityQueue<Node> trie = new PriorityQueue<>();
		
		for(Map.Entry<Character, Integer> entry : this.charToInt.entrySet()) {
			trie.add(new Node(entry.getKey(), entry.getValue(),null,null));
		}
		
		
		while(trie.size() > 1) {
			Node left = trie.poll();
			Node right = trie.poll();
			Node root = new Node('\0',left.freq + right.freq,left,right);
			trie.add(root);
		}
		
		return trie.poll(); //return the root node
	}
	
	
	
	
	public void setMaps() {
		setMaps(this.rootTrie,"");
	}
	
	private void setMaps(Node root, String code) {
		if(root == null) return;
		setMaps(root.left,code + '0');
		setMaps(root.right,code + '1');
		if(root.isLeaf()) {
			this.charToCode.put(root.c, code);
			this.codeToChar.put(code, root.c);
		}
	}

	
	public String getCodeFromChar(Character c) {
		return this.charToCode.get(c);
	}
	
	public Character getCharFromCode(String code) {
		return this.codeToChar.get(code);
	}
	
	
	public void setPreOrderCode() {
		this.preOrderCode = getPreOrderCode(this.rootTrie,"");
		
	}
	
	public String getPreOrderCode(Node node, String code) {
		if(node == null) return code;
		if(node.isLeaf()) {
			code = code + "1" + node.c;
			return code;
		}
		code = code + "0";
		code = getPreOrderCode(node.left, code);
		code = getPreOrderCode(node.right, code);
		return code;
	}
	
	public void writeFile()throws IOException  {
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("compressed.txt"));
		
		writeFile(output);
		
		output.close();
	}
	
	public static String toBinaryCode(String code) {
		String result = new String();
		String temp;
		for(int i = 0; i < code.length();i++) {
			if(code.charAt(i) != '0' && code.charAt(i) != '1') { //verificacao de char (a,b,c,...)
				temp = Integer.toBinaryString(code.charAt(i));
				int toEightBits = 8 - temp.length();
				
				while(toEightBits != 0) { //Completar os 8 bits
					temp = "0" + temp;
					toEightBits--;
				}
				result = result + temp;
			}
			else result = result + code.charAt(i);
		}
		return result;
	}
	

	
	private void writeTrie(byte[] bytes, int[] i_count, String binaryCode) {

		System.out.println("binaryCode (from preOrder): " + binaryCode);
						
		int indexString = 0;

		while(indexString < binaryCode.length()) {

			if(binaryCode.charAt(indexString) == '1') {
				bytes[i_count[0]] <<= 1;
				bytes[i_count[0]] |= 1;
			}
			else bytes[i_count[0]] <<= 1;
			
			i_count[1]++;
			indexString++;
			if(i_count[1] == 8) {
				i_count[1] = 0;
				i_count[0]++;
			}
					
		}		
	}
	

	private void writeBinaryInt(byte[] bytes, int[] i_count) {
		


		int charsNumber =  this.rootTrie.freq;
		
		String code = Integer.toBinaryString(charsNumber);
		
		int size = code.length();
		
		//Adicionar os "bits" restantes na string code para que tenhamos 32 bits
		while(size != 32) {
			code = "0" + code;
			size++;
		} 
		

		int stringIndex = 0;
		while(stringIndex < code.length()) {
			if(code.charAt(stringIndex) == '1') {
				bytes[i_count[0]] <<= 1;
				bytes[i_count[0]] |= 1;
			}
			else bytes[i_count[0]] <<=1;
			
			stringIndex++;
			i_count[1]++;

			if(i_count[1] == 8) {
				i_count[1] = 0;
				i_count[0]++;
			}
		}

	}
	
	
	
	 private void writeMessage(byte[] bytes, int[] i_count) {

		 int indexString = 0;
		 
		 //this.code eh a mensagem codificada concatenada ja - eh uma string
		 while(indexString < this.code.length()) {
			if(this.code.charAt(indexString) == '1') {
				bytes[i_count[0]] <<= 1;
				bytes[i_count[0]] |= 1;
			}
			else bytes[i_count[0]] <<= 1;
			
			i_count[1]++;
			indexString++;
				
			if(i_count[1] == 8) {
				i_count[1] = 0;
				i_count[0]++;
			}
		}
		
		
	 }
	
	//Funcao para realizacao da escrita do vetor "bytes" no arquivo compactado
	private void writeFile(ObjectOutputStream out) throws IOException {
		String binaryCode = Huffman.toBinaryCode(this.preOrderCode); //arvore codificada
		
		int size = (int) Math.ceil((double) binaryCode.length()/8); //tamanho - arvore codificada
		int size2 = (int) Math.ceil((double) this.code.length()/8); //tamanho - mensagem codificada
		
		int sizeVetor = size + size2 + 4;
		
		byte[] bytes = new byte[sizeVetor];
		int[] i_count = new int[2];

		
		this.writeTrie(bytes,i_count,binaryCode);
		this.writeBinaryInt(bytes,i_count);
		this.writeMessage(bytes,i_count);  
		
		
		//Left-shift nos bits do ultimo byte
		if(i_count[1] < 8) { 
			int remaining = 8 - i_count[1];
			bytes[i_count[0]] <<= remaining;
		}

		out.write(bytes); //Escrever vetor de bytes no arquivo comprimido (compressed.txt)
	}
	
	
	//Funcao para ler o inteiro escrito no arquivo compactado
	private int readLength(ObjectInputStream in) throws IOException {
		
		/*
		 * No arquivo comprimido, o inteiro esta salvo como um vetor de 4 bytes
		 * Iremos pegar cada bit e, logo em seguida, converter o vetor de bytes em um inteiro
		 */
		
		byte[] intByte = new byte[4];
		int bytePos = 0;
		int count = 0;
		boolean bit;
		for(int i = 0; i < 32;i++) {
			
			bit = getBit(in);
			if(bit) {

				intByte[bytePos] <<= 1;
				intByte[bytePos] |= 1;
				
			}else {

				intByte[bytePos] <<= 1;
			}
			count++;
			if(count == 8) {
				bytePos++;
				count = 0;
			}

		}
	
		//Numero de bytes da mensagem comprimida
		int numberBytes = ByteBuffer.wrap(intByte).getInt();
		return numberBytes;
	}
	
	
	
	
	
	//Funcao para descompactar o arquivo compactado
	public void descompress() throws IOException {
		ObjectInputStream in = new ObjectInputStream(new FileInputStream("compressed.txt"));

		Node root = readTrie(in);

		int bytesNumber = readLength(in);
	
		System.out.println("Inteiro lido (numero de bytes da mensagem comprimida): " + bytesNumber);

		//Imprimir a mensagem codificada na tela:
		System.out.print("Mensagem codificada: ");
		boolean bit;
		for(int i = 0; i < bytesNumber;i++) {
			Node curr = root;
			while(!curr.isLeaf()) {
				bit = getBit(in);
				if(bit) curr = curr.right;
				else curr = curr.left;
			}
			System.out.print(curr.c);
		}
		
		
		//for-loop, de 0 ate o int gravado, para ler os bytes da mensagem comprimida
		//Iremos ler um byte por vez e realizar os percursos na arvore construida
		//Quando chegarmos numa folha, imprime o caractere e volte para a raiz da arvore

		in.close();
	}
	

	static int positionByte = -1;
	static byte currentByte;
	
	//Funcao que retorna o proximo bit de um byte
	public boolean getBit(ObjectInputStream in) throws IOException{

		if(positionByte == -1) {
			currentByte = in.readByte();
			positionByte = 7;
		}	

		boolean bit = ((currentByte >> positionByte) & 1) != 0;
		positionByte--;

		return bit;
	}
	
	
	//Ler a arvore de huffman que esta salva no arquivo "compressed.txt"
	//Retorna-se, no fim, o no raiz da arvore
	private Node readTrie(ObjectInputStream in) throws IOException{
		
		boolean bit = getBit(in);
		if(bit) {

			int i = 0;
			String temp = new String();

			while(i < 8) {
				bit = getBit(in);
				if(bit) 
					temp = temp + "1";
				else temp = temp + "0";
				i++;
			}

			char c = (char) Integer.parseInt(temp, 2);
			return new Node(c,-1,null,null);
		}
		else {
			return new Node('\0',-1,readTrie(in),readTrie(in));
		}
	}
	

	//Criar string com o codigo comprimido da mensagem inicial
	public void concatenateCode() {
		this.code = concatenateCode(this.charToCode);
		System.out.println("Compressed message: " + this.code);
	}
	private String concatenateCode(Map<Character,String> mapping) {
		String result = new String();
		
		for(int i = 0; i < this.fileText.length();i++) 
			result += mapping.get(this.fileText.charAt(i));

		return result;
	}
	

	
	
	public void print() {
		System.out.println("Huffman trie:");
		print(this.rootTrie,1,"");
	}
	
	private void print(Node root, int level, String tab) {
		if(root == null) return;
		
		print(root.right,level+1,tab + "\t");
		
		if(root.isLeaf()) 
			System.out.println(tab + root.c + "[Level:" + level + "]");
		else 
			System.out.println(tab + root.freq + "[Level:" + level + "]");
		
		
		print(root.left,level+1,tab + "\t");
	}
}
