//---------------------------------------------------------------------------

#include <fstream.h>
#include <vcl.h>
#include <iostream.h>
#include <conio.h>
#pragma hdrstop

#include <tchar.h>
//---------------------------------------------------------------------------

void BuchLaden(void);
void entferne(void);
int convert(__int64 f1, __int64 f2);
void toHuffman(void);
void sotiere(void);
int compare(const void *a, const void *b);
void testPrint(void);
void writeToFile(void);
void testRead(void);

struct OPENINGvorlage {
		unsigned __int64 FeldData1;
		unsigned __int64 FeldData2;
		short Win;
	}*OPENING, *NOPENING;

int* huff;
int newSize;

#pragma argsused
int _tmain(int argc, _TCHAR* argv[])
{
	BuchLaden();
	entferne();
	toHuffman();
	sotiere();
	testPrint();
	writeToFile();
	//testRead();
	getch();
	return 0;
}
//---------------------------------------------------------------------------

void BuchLaden(void)
{
	cout << "Buchladen : " << "\n";
	/*Öffnet die Datenbank(die Datenbank muss sich im gleichen Verzeichnis wie die Anwendung befinden)
	und lädt die Stellungen in die Variablen FeldData1 und FeldData2. Die Bewertungen der Stellungen
	werden im Array Win[] gespeichert.*/
	int dateigroesse;
	long k=0;
	AnsiString pfad=ExtractFileDir(Application->ExeName)+"\\12ply.dat"; //Pfad der Datenbank

	OPENING = new OPENINGvorlage[4200899];
	ifstream fdatei;
	fdatei.open(pfad.c_str(), ios::in | ios::binary);
	if(fdatei.fail())
	{
		cout << "12-Steine Datenbank konnt nicht geöffnet werden. Das Programm wird beendet!!!";
		exit(0);
	}

	fdatei.seekg(0, ios::beg);
	while(k<4200899)
	{
		fdatei.read(reinterpret_cast<char *> (&OPENING[k].FeldData1), sizeof(__int64));
		fdatei.read(reinterpret_cast<char *> (&OPENING[k].FeldData2), sizeof(__int64));
		fdatei.read(reinterpret_cast<char *> (&	OPENING[k++].Win), sizeof(BYTE));
	}

	//OPENING[k].FeldData1=-1; //mit Endekennung zu vergleichen
	//OPENING[k].FeldData2=-1; //mit Endekennung zu vergleichen
	//OPENING[k].Win=-1;
	fdatei.close();
}

void entferne(void) {
	cout << "Entferne: Zähle" << "\n";
	int j=0;
	for(int k=0;k<4200899;k++)
		if(OPENING[k].Win != 2)
			j++;
	cout << "Anzahl: " << j << "\n";

	NOPENING = new OPENINGvorlage[j];

	cout << "Entferne: Kopiere" << "\n";
	j=0;
	for(int k=0;k<4200899;k++)
		if(OPENING[k].Win != 2)
			NOPENING[j++] = OPENING[k];
	cout << "Kopiert: " << j << "\n";
	newSize = j;
}

int convert(__int64 f1, __int64 f2) {
	int temp=0, j;
	__int64 mask;

	//Stellung Codieren, Huffmann Code
	for(j=41;j>=0;j--) {
		mask = 1i64 << j;
		if(f1 & mask) {
			temp <<= 2;
			temp |= 3;
		}
		else if(f2 & mask) {
			temp <<= 2;
			temp |= 2;
		}
		if( !(j%6))
			temp <<= 1;
	}
	return temp << 1;
}

void toHuffman(void) {
	cout << "toHuffman\n";
	huff = new int[newSize];
	for(int k=0;k<newSize;k++)
		huff[k] = (convert(NOPENING[k].FeldData1, NOPENING[k].FeldData2) | NOPENING[k].Win);
	cout << "toHuffman: " << newSize << " Positions converted\n";
}

void sotiere(void) {
	cout << "Sortiere: \n";
    qsort(huff, newSize, sizeof(int), compare);
}

int compare(const void *a, const void *b) {
    //return ( *(int*)a - *(int*)b );
	int *aa = (int *)a;
	int *bb = (int *)b;

	__int64 aaa = *aa;
	__int64 bbb = *bb;
	if(aaa > bbb)
		return 1;
	else if (bbb > aaa)
		return -1;
	return 0;
}

void testPrint(void) {
	cout << "TestPrint\n";
	for(int k=0;k<newSize;k+=10000)
		cout << huff[k] << endl;
}

void writeToFile(void) {
	cout << "\nWriteToFile\n";
	ofstream fdatei;
	fdatei.open("C:\\10\\huff.dat", ios::out | ios::binary);
	for(int k=0;k<newSize;k++) {
		//Andere Byte-Reihenfolge
		BYTE b0 =  (huff[k] >> 24) & 0xFF;
		BYTE b1 =  (huff[k] >> 16) & 0xFF;
		BYTE b2 =  (huff[k] >> 8) & 0xFF;
		BYTE b3 =  huff[k] & 0xFF;
		fdatei.write(reinterpret_cast<char *> (&b0), sizeof(BYTE));
		fdatei.write(reinterpret_cast<char *> (&b1), sizeof(BYTE));
		fdatei.write(reinterpret_cast<char *> (&b2), sizeof(BYTE));
		fdatei.write(reinterpret_cast<char *> (&b3), sizeof(BYTE));
	}
	fdatei.close();
}

void testRead(void) {
	cout << "testRead: " << endl;
	int x;
	ifstream fdatei;
	fdatei.open("C:\\10\\huff.dat", ios::in | ios::binary);
	for(int k=0;k<newSize;k++) {
		fdatei.read(reinterpret_cast<char *> (&x), sizeof(int));
		if(huff[k] != x)
        	cout << huff[k] << ", " << x << "\n";
	}
	fdatei.close();
}
