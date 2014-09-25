//---------------------------------------------------------------------------

#include <vcl.h>
#include <fstream.h>
#include <conio.h>
#include <iostream.h>
#pragma hdrstop

//---------------------------------------------------------------------------

#pragma argsused

int einlesen(void);
__int64 CharToInt64(char *string);
int ausgeben(void);
int konvertierung(void);
int konvertierungeinlesen(void);
__int64 feldgespiegelt(__int64 Feld);
unsigned __int64 HeightErmitteln(__int64 Feld1, __int64 Feld2);
void spiegelung(void);

const unsigned __int64 zufall[2][42]=
								{2704506115994628i64,
								886077597871704230i64,
								49389502572435258i64,
								82333996817139652i64,
								263967204879563328i64,
								81975673952415600i64,
								73398886193708064i64,
								1595863713887220963i64,
								46261610206381440i64,
								58705059883690202i64,
								231696507446129885i64,
								22606427398883328i64,
								12595346104058426i64,
								4097820997223100i64,
								101324622437045280i64,
								31779374605300125i64,
								35633797708573350i64,
								22416112427922675i64,
								36363358841386824i64,
								4173667863902779612i64,
								9131869703157656i64,
								14138249969764235i64,
								214348955873908032i64,
								58547228054472360i64,
								55740094356093127i64,
								1777939723684020i64,
								362858203316162568i64,
								28975890403315160i64,
								1242349240448115806i64,
								59601464106441712i64,
								9110872168202946i64,
								10631234269963860i64,
								16888881020037981i64,
								1159792823631456i64,
								36205525950397736i64,
								47068546447093808i64,
								375817237236357603i64,
								32189775283681470i64,
								1493718293429439i64,
								20793138156733824i64,
								101478045365676084i64,
								110552240521049760i64,
								19298943901485610i64,
								6548220796761019i64,
								1777628907278452803i64,
								11891769178478592i64,
								3564258696970080i64,
								236708853179436288i64,
								349182760342233125i64,
								429289086240375i64,
								121921717543355343i64,
								31495429917193824i64,
								5694462647075520i64,
								30758051047680284i64,
								1365712501364505581i64,
								17363511325679223i64,
								119226791868480i64,
								6220173073409360i64,
								11647770880598424i64,
								24507207907919492i64,
								551903736927872i64,
								2097977134396858i64,
								3108717381973636075i64,
								25389306976498643i64,
								254362479754036508i64,
								119080142037405540i64,
								65628472867223040i64,
								116416206906490816i64,
								130703539652185785i64,
								1541174198942728362i64,
								37852277734190556i64,
								22426187114508354i64,
								290694253237906321i64,
								3460150747465650i64,
								12108862858045894i64,
								124792798959719156i64,
								6572334569141376i64,
								2726416766762766i64, 
								378828340783306008i64,
								72087612995472200i64,
								113983283880328672i64,
								376285078915283i64,
								62397498210717124i64,
								1193066389676430i64};



int main(int argc, char* argv[])
{
	//einlesen();
	//ausgeben();
	//getch();
	konvertierung();
	//spiegelung();
	return 0;
}
//---------------------------------------------------------------------------


int einlesen(void)
{
	ifstream fdatei;
	ofstream odatei;
	const char *datei="C:\\1.txt";
	fdatei.open(datei, ios::in);
	odatei.open("L:\\2.txt",ios::out | ios::binary);
	//odatei.open("L:\\2.txt",ios::out);
	char temp[30];
	__int64 fu;
	for(long i=0;!fdatei.eof();i++)
	{
		fdatei >> temp;
		fu=CharToInt64(temp);
		odatei.write(reinterpret_cast<char *> (&fu), sizeof(__int64));
	}
	odatei.close();
	return 0;
}

int ausgeben(void)
{
	ifstream fdatei;
	ofstream odatei;
	const char *datei="L:\\2.txt";
	fdatei.open(datei, ios::in | ios::binary);
	odatei.open("L:\\3.txt",ios::out);
	char temp[30];
	__int64 fu;
	for(long i=0;!fdatei.eof();i++)
	{
		fdatei.read(reinterpret_cast<char *> (&fu), sizeof(__int64));
		//odatei << fu;
	}
	odatei.close();
	return 0;
}
void spiegelung(void)
{
	ifstream fdatei;
	ofstream odatei;
	int i,j;
	long zaehler=0;
	__int64 Feld1, Feld2, Feld1Gespiegelt, Feld2Gespiegelt;
	char temp[100], temp1[100], temp2[100];
	__int64 *a, *b;
	a=new __int64[1231038];
	b=new __int64[1231038];
	const char *datei="C:\\1\\1.txt";
	fdatei.open(datei, ios::in);
	odatei.open("C:\\1\\2.txt",ios::out);
	while(!fdatei.eof())
	{
		fdatei >> temp;
		i=0;
		while(temp[i]!=',')
		{
			temp1[i]=temp[i];
			i++;
		}
		temp1[i]='\0';
		i++;
		j=0;
		do
		{
				temp2[j]=temp[i];
				j++;i++;
		}
		while(temp[i]!='\0');
		temp2[j]='\0';

		Feld1=CharToInt64(temp1);
		Feld2=CharToInt64(temp2);
		a[zaehler]=Feld1;
		b[zaehler++]=Feld2;
	}
	for(long k=0;k<1231038;k++)
	{
		if((k % 1000)==0)
			cout << k << endl;
		if(a[k]>0)
		{
			Feld1Gespiegelt=feldgespiegelt(a[k]);
			Feld2Gespiegelt=feldgespiegelt(b[k]);
			for(long l=k+1;l<1231038;l++)
			{
				if(a[l]==Feld1Gespiegelt && b[l]==Feld2Gespiegelt)
				{
					a[l]=-1;
					b[l]=-1;
					break;
                }
            }
        }
	}
	for(long k=0;k<1231038;k++)
	{
		if(a[k]>0)
        	odatei << a[k] << "," << b[k] << "\n";
    }
}

__int64 feldgespiegelt(__int64 Feld)
{
		__int64 Feld1Gespiegelt=0i64;
		 int stelle1, stelle2,i,j,l,m;
         long k;

         for(j=0,m=6;j<7 && m>=0;j++,m--)
         	for(i=0;i<6;i++)
            {
               	stelle1=41-(m*6+i);
                stelle2=41-(j*6+i);
				if((1i64<<stelle1 & Feld) == 1i64<<stelle1)
					Feld1Gespiegelt |= 1i64 << stelle2;
			}
		 return Feld1Gespiegelt;
}
int konvertierungeinlesen(void)
{
	  clock_t start, end;
	start = clock();
	ifstream fdatei;
	const char *datei1="C:\\konv.txt";
	__int64 feld1, feld2;
	BYTE wert;
	fdatei.open(datei1, ios::in | ios::binary);
	while(!fdatei.eof())
	{
		fdatei.read(reinterpret_cast<char *> (&feld1), sizeof(__int64));
		fdatei.read(reinterpret_cast<char *> (&feld2), sizeof(__int64));
		fdatei.read(reinterpret_cast<char *> (&wert), sizeof(BYTE));
	}
	end = clock();
	double diff=end-start;
	cout << diff/CLOCKS_PER_SEC;
	getch();
}

int konvertierung(void)
{
	int i, j;
	ifstream fdatei;
	ofstream odatei;
	const char *datei1="C:\\1\\qsort.txt";
	const char *datei2="C:\\1\\konv.txt";

	fdatei.open(datei1, ios::in);
	odatei.open(datei2, ios::out | ios::binary);
	char temp[100], temp1[100], temp2[100], temp3;
	__int64 futz;
	Byte wert;
	long c=0;
	while(!fdatei.eof())
	{
		fdatei >> temp;
		i=0;
		while(temp[i]!=',')
		{
			temp1[i]=temp[i];
			i++;
		}
		temp1[i]='\0';
		i++;
		j=0;
		do
		{
				temp2[j]=temp[i];
				j++;i++;
		}while(temp[i]!='\0');
		temp2[j-1]='\0';
		temp3=temp[i-1];
		if(temp3=='+')
			wert=2;
		else if(temp3=='-')
			wert=1;
		else if(temp3=='=')
			wert=0;
		else
		{
			cout << "Error!!!";
			getch();
		}
		futz=CharToInt64(temp1);
		odatei.write(reinterpret_cast<char *> (&futz), sizeof(__int64));
		futz=CharToInt64(temp2);
		odatei.write(reinterpret_cast<char *> (&futz), sizeof(__int64));
		odatei.write(reinterpret_cast<char *> (&wert), sizeof(BYTE));
		c++;
	}
}

__int64 CharToInt64(char *string)
{
    int i;
    __int64 potenz=1i64;
    __int64 ret=0i64;
    for(i=0;i<20;i++)
    {
        if(string[i]==0)
            break;
    }
    for(--i;i>=0;i--)
    {
        ret+= (__int64)(string[i]-48i64)*potenz;
        potenz*=10;
    }
    return ret;
}

unsigned __int64 HeightErmitteln(__int64 Feld1, __int64 Feld2)
{
    int i,j, stelle;
    __int64 temp;
    unsigned __int64 ZobristKey=0i64;
    for(i=0;i<7;i++)
    {
        for(j=0;j<6;j++)
        {
            stelle=41-(i*6+j);
            temp = (1i64 << stelle);
			if((Feld1 & temp)==temp)
                ZobristKey^=zufall[0][i*6+j];
            else if((Feld2 & temp)==temp)
                ZobristKey^=zufall[1][i*6+j];
            else
                break;
        }
        //Height[i]=j;
    }
    return ZobristKey;
}