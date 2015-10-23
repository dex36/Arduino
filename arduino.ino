#include <SoftwareSerial.h>

#define DEBUG true

SoftwareSerial esp8266(2, 3);

void setup()
{
	Serial.begin(9600); 
	esp8266.begin(9600); 

	pinMode(11, OUTPUT);
	digitalWrite(11, LOW);
	pinMode(12, OUTPUT);
	digitalWrite(12, LOW);
	pinMode(13, OUTPUT);
	digitalWrite(13, LOW);
	pinMode(10, OUTPUT);
	digitalWrite(10, LOW);
	pinMode(5, OUTPUT); /// Speed
	pinMode(A0, INPUT);


	sendCommand("AT+RST\r\n", 2000, DEBUG); // reset module
	sendCommand("AT+CWMODE=1\r\n", 1000, DEBUG); // configure as access point
	sendCommand("AT+CWJAP=\"sagos\",\"12345678\"\r\n", 3000, DEBUG);
	delay(5000);
	sendCommand("AT+CIFSR\r\n", 1000, DEBUG); // get ip address
	sendCommand("AT+CIPMUX=1\r\n", 1000, DEBUG); // configure for multiple connections
	sendCommand("AT+CIPSERVER=1,80\r\n", 1000, DEBUG); // turn on server on port 80

	Serial.println("Server Ready");
}

void loop()
{
	if (esp8266.available()) // check if the esp is sending a message 
	{
		if (esp8266.find("+IPD,"))
		{
			delay(100); 
			int connectionId = esp8266.read() - 48; 

			esp8266.find("pin="); // advance cursor to "pin="

			int pinNumber = (esp8266.read() - 48); 
			int secondNumber = (esp8266.read() - 48);
			if (secondNumber >= 0 && secondNumber <= 9)
			{
				pinNumber *= 10;
				pinNumber += secondNumber; 
			}
			digitalWrite(pinNumber, !digitalRead(pinNumber)); // toggle pin    

			/////////////////////////////////////////////////
			esp8266.find("val=");
			int pinNumber2 = (esp8266.read() - 48);
			int secondNumber2 = (esp8266.read() - 48);
			if (secondNumber2 >= 0 && secondNumber2 <= 9)
			{
				pinNumber2 *= 10;
				pinNumber2 += secondNumber;
			}
			Serial.print(pinNumber2);
			Serial.write("\r\n");
			analogWrite(5, pinNumber2*2.5);
			delay(100);
			//jest do 100 a max jest do 255
			/**
			for (int speed = 0; speed < pinNumber2; speed++){
				analogWrite(5, speed);
				delay(50);
				//Serial.print(speed);
				//Serial.write("\r\n");		
			}	
			**/

			Serial.print(analogRead(A0));
			Serial.write("\r\n");
		    ///////////////////////////////////////////////




			String content;
			content = "Pin ";
			content += pinNumber;
			content += " is ";
			if (digitalRead(pinNumber)){content += "ON ";}
			else{content += "OFF ";}

			// OPEN CONNECTION
			String cipSend = "AT+CIPSEND=";
			cipSend += connectionId;
			cipSend += ",";
			cipSend += content.length();
			cipSend += "\r\n";

			sendCommand(cipSend, 100, DEBUG);
			sendData(content, 100, DEBUG);

			// CLOSE CONNECTIO
			String closeCommand = "AT+CIPCLOSE=";
			closeCommand += connectionId; // append connection id
			closeCommand += "\r\n";
			sendCommand(closeCommand, 100, DEBUG); // close connection

		}
	}
}

String sendData(String command, const int timeout, boolean debug)
{
	String response = "";
	int dataSize = command.length();
	char data[dataSize];
	command.toCharArray(data, dataSize);
	esp8266.write(data, dataSize); 
	if (debug)
	{
		Serial.write(data, dataSize);
		Serial.write("\r\n");
	}
	long int time = millis();
	while ((time + timeout) > millis())
	{
		while (esp8266.available())
		{
			char c = esp8266.read(); 
			response += c;
		}
	}
	return response;
}

String sendCommand(String command, const int timeout, boolean debug)
{
	String response = "";
	esp8266.print(command); 
	long int time = millis();
	while ((time + timeout) > millis())
	{
		while (esp8266.available())
		{
			char c = esp8266.read(); 
			response += c;
		}
	}
	if (debug)
	{
		Serial.print(response);
	}
	return response;
}
