# NoobParticipants

This project is based on the technology of OCR (Optical Character Recognisation).
Technology Used : OCR, Android
Tools Used: Android Studio
Library & Api Used:  Microsoft vision api

## Setup : 
Step 1. -> Clone the apps to your system and open with Android Studio (or Similar IDE)

Step 2. -> Go to  https://azure.microsoft.com/en-in/services/cognitive-services/computer-vision/  to obtain the Microsoft vision  api key along with root url

Step 3. -> open strings.xml file and place your Api key and root url 
			app -> src -> main -> res -> values -> strings.xml
			
Step 4. -> Run the app 

### Application Description:-

 * The purpose of the application is to easen (automate) the task of data entry from similar types of images. 
 * The application takes images as input and outputs the required table
 * The columns of the table are described by the user and he/she is given with the choice of strings to be put in respective columns.
 * This is done only for a sample image and other similar images are uploaded. The table is now filled by the machine.
 * The app uses Microsoft's OCR API for reading texts from images.

Demerits till now :-
 
 * There are some 2000 images recognised by the API mentioned above. Other than those, our system fails to read text from image.
 * Some cases have occured where symbols in images affects the output. Those cases haven't been handled yet but we are trying our best to come up with something enhanced and powerful 
  

## Future Scope of Project :- 
  
 * The future scope of project is its acceptability in the market as a completely functional application for the purpose of data entry from similar images. For that,
   we are looking forward to fix the issues mentioned above.
 * Future scope in the market -> Once ready,the application can be sold directly to users or to the third party who would like to provide any services to their customers
   that our application can help with.
 * The application can also be implemented in a different way to be used for image verification.  
 
## License & copyright
Licensed under the [MIT License](LICENSE).
