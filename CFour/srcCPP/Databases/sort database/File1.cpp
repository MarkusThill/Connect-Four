//---------------------------------------------------------------------------

#include <vcl.h>
#include <fstream.h>
#pragma hdrstop

//---------------------------------------------------------------------------

#pragma argsused

#define GROESSE 635477

//char  **werte;
char werte[GROESSE][50];

void laden(void);
int compare(const void *a, const void *b);
int FindeKomma(char *string);
void ausgabe(void);
__int64 CharToInt64dec(char *string);

int main(int argc, char* argv[])
{
	laden();
	qsort(werte, GROESSE, sizeof(char)*50, compare);
	ausgabe();
	return 0;
}
//---------------------------------------------------------------------------


void laden(void)
{
	ifstream file;
	char zeile1[50];
	const char *dateiname="C:\\1\\10.txt";
	long i=0;
	file.open(dateiname, ios::in);
	while(!file.eof())
	{
		file >> zeile1;
		strcpy(werte[i++],zeile1);
	}
	file.close();
}

int compare(const void *a, const void *b)
{
	char *zeile2, *zeile3;

	int x,y;
	x=FindeKomma((char *)a);
	y=FindeKomma((char *)b);
	if(x>y)
		return 1;
	else if(x<y)
		return -1;
	else if(x==y)
	{
    	char z1[50], z2[50];
		strcpy(z1,(char *)a);
		strcpy(z2,(char *)b);
		z1[x]='\0';
		z2[y]='\0';
		__int64 Feld11, Feld12;
		Feld11=CharToInt64dec(z1);
		Feld12=CharToInt64dec(z2);
		if(Feld11>Feld12)
			return 1;
		else if(Feld11<Feld12)
			return -1;
		else if(Feld11==Feld12)
		{
			zeile2 = strchr((char *)a,',')+1;
			zeile3 = strchr((char *)b,',')+1;
			if(CharToInt64dec(zeile2)>CharToInt64dec(zeile3))
				return 1;
			else
				return -1;
        }
	}
	return (strcmp( (char*)a, (char*)b));
}

int FindeKomma(char *string)
{
	for(short i=0;i<50;i++)
	{
		if(string[i]==',')
			return i;
	}
	return (-1);
}

void ausgabe(void)
{
		ofstream file;
		const char *dateiname="C:\\1\\qsort.txt";
		file.open(dateiname, ios::out);
		for(long i=0;i<GROESSE;i++)
		{
			file << werte[i] << "\n";
		}
		file.close();
}

__int64 CharToInt64dec(char *string)
{
    int i;
    __int64 potenz=0i64;
    __int64 ret=0i64;
    for(i=0;i<20;i++)
    {
        if(string[i]==0)
            break;
    }
    for(--i;i>=0;i--)
		ret+=(__int64)(string[i]-48i64)*pow((double)10,(double)potenz++);
    return ret;
}
