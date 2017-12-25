

import java.io.*;
//import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import javax.crypto.Cipher;
import javax.swing.JOptionPane;
public class RSAPublicKeyBob {

	public static void main(String[] args) throws Exception {

	    int port = 7999;
		ServerSocket s = new ServerSocket(port);
		Socket client = s.accept();
		

		ObjectInputStream alicePublicKey = new ObjectInputStream(client.getInputStream());
		ObjectOutputStream bobPublicKeyStream = new ObjectOutputStream(client.getOutputStream());
		
		//Generating bob's private and public key
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
	    keyPairGen.initialize(1024, new SecureRandom()); 
	    KeyPair keyPair = keyPairGen.genKeyPair();
	    RSAPublicKey bobPublicKey_Bob = (RSAPublicKey) keyPair.getPublic();
	    RSAPrivateKey bobPrivateKey = (RSAPrivateKey)keyPair.getPrivate();
	    
	    //sending bob's public Key To Alice
	    bobPublicKeyStream.writeObject(bobPublicKey_Bob);
	    
	    //receiving alice public key from bob
	    RSAPublicKey alicePublicKeyReception = (RSAPublicKey) alicePublicKey.readObject();
	    Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
	    
	    int chosenService = alicePublicKey.readInt();
	    
			  if(chosenService == 1) {
				  //Confidentiality: deciphering using bob's private key 
				  byte[] receivedEncryptedMessage = (byte[]) alicePublicKey.readObject();
				  cipher.init(Cipher.DECRYPT_MODE, bobPrivateKey);
   			      byte[] decryptedMessage = cipher.doFinal(receivedEncryptedMessage);
   			      JOptionPane.showMessageDialog(null,  "Confidentiality: The Message has been " +
   			      		"decrypted with bob's private key \n" 
   			    		  + " the Original Message is: " + new String(decryptedMessage) );
			  }
			  
			  if(chosenService == 2) {
				  //Integrity&Authentication: Deciphering with Alice public key  
				  byte[] receivedEncryptedMessageI = (byte[]) alicePublicKey.readObject();
				  cipher.init(Cipher.DECRYPT_MODE, alicePublicKeyReception);
   			      byte[] decryptedMessageI = cipher.doFinal(receivedEncryptedMessageI);
   			     JOptionPane.showMessageDialog(null,  "Integrity & Authetication: The Message has been " +
  			      		"decrypted with alice's public key \n" 
  			    		  + " the Original Message is: " + new String(decryptedMessageI) );
			  }
				  
			  if(chosenService == 3) {
				  //Both: Deciphering with bob private key first, and then with alice public key.   
				  byte[] receivedEncryptedMessageB = (byte[]) alicePublicKey.readObject();
				  cipher.init(Cipher.DECRYPT_MODE,bobPrivateKey);
   			      byte[] decryptedMessageB = cipher.doFinal(receivedEncryptedMessageB);
   			      //String y = new String(decryptedMessageB);
   			      cipher.init(Cipher.DECRYPT_MODE,alicePublicKeyReception);
   			      byte[] decryptedMessage2 = cipher.doFinal(decryptedMessageB);
   			      
   			     JOptionPane.showMessageDialog(null,  "Both: The Message has been firstly" +
  			      		"decrypted with bob's private key and then alice public key \n" 
  			    		  + " the Original Message is: " + new String(decryptedMessage2) );
			  }
		}
		
	}
 