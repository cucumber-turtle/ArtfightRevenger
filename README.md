# Artfight Revenger
Java program to find "revenges" that need to be completed and records them. Created with the permission of the Artfight site moderators.

### Disclaimer
Please use this program sparingly. It is not meant to be used to mass revenge or keep track of in-season attacks (although it can, but so can you), but rather to find revenges that you may have missed in previous seasons. If you use this program too frequently, you may get rate limited. 

## What is Artfight?
Artfight is a website where artists can create characters and attack other artists' characters. You can learn more about Artfight [here](https://artfight.net/info/about).

## How does it work?
Artfight Revenger sends requests to the Artfight website and parses the HTML to find revenges that need to be completed. It then records the revenges in a CSV (comma-separated value) file. ArtfightRevenger uses the authentication information that you give it, and does not store that information anywhere outside your machine. It is a web scraper made specifically to automate finding attacks that have not been revenged by the authenticated user yet.

## How do I use it?
1. Fork and clone this repository, and open it in your IDE of choice.
2. Copy the `authenticate-template.json` with your own authentication information in the `resources` directory.
3. The program does not need any arguments to run, so you can run it from your IDE or compile it and run it from the command line.
4. The program will create a CSV file in the root directory with the name `my-revenges.csv`. This file will contain the revenges that need to be completed.

## How do I get my authentication information?
It will just be your Artfight username and password, if you have an account. You can get your username from your profile page, and your password is the password you use to log in to Artfight.

## It's not working!
If you are having trouble with the program, please open an issue on this repository.
