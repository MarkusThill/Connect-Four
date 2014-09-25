//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "Unit1.h"

#define SPIELER1 true
#define SPIELER2 false
#define HASHSIZE 49999999i64
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"

#include "Gewinnstellungen.cpp"
#include "Spielbeenden.cpp"
TForm1 *Form1;
//---------------------------------------------------------------------------

unsigned __int64 zufall[2][42]= {2704506115994628i64,
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

__fastcall TForm1::TForm1(TComponent* Owner)
    : TForm(Owner)
{
    randomize();
    a = (__int64 *)malloc(HASHSIZE * sizeof(__int64));
    b = (__int64 *)malloc(HASHSIZE * sizeof(__int64));
    for(long i=0; i<HASHSIZE; i++)
    {
    	a[i]=-1i64;
        b[i]=-1i64;
    }
    Feld1=0i64;
    Feld2=0i64;

    //HIER WIRD die ANZAHL DER STEINE FESTGELEGT
    maxinstance=10;
    zaehler=0;
    for(int i=0;i<7;i++)
        Height[i]=0;
    Edit1->Text=zufall[1][0];
}
//---------------------------------------------------------------------------

inline void TForm1::SteinSetzen(int x, Boolean spieler)
{
	/*Setzt einen Stein im Bitboard für den betreffenden Spieler*/
	int stelle=41-(x*6+Height[x]++); //Das Bit, was im Bitboard gesetzt werden muss
    if(spieler==SPIELER1)
    	Feld1 |= 1i64 << stelle;
    else
    	Feld2 |= 1i64 << stelle;
}

inline void TForm1::SteinLoeschen(int x, Boolean spieler)
{
	/*Löscht einen Stein im Bitboard für den betreffenden Spieler*/
	int stelle=41-(--Height[x]+x*6); //Das Bit, was im Bitboard gelöscht werden muss
    if(spieler==SPIELER1)
    	Feld1 &= ~(1i64<<stelle);
    else
    	Feld2 &= ~(1i64<<stelle);
}

void TForm1::Spielbaum1(int instance, unsigned __int64 ZKey)
{
    if(SpielBeenden1())
        return;

    if(instance==maxinstance)
    {
        //if(!BereitsVorhanden(Feld1, Feld2))
        long zaehler = long(ZKey % HASHSIZE);
        {
            a[zaehler]=Feld1;
            b[zaehler]=Feld2;
        }
        return;
    }
    int x;
    for(x=0;x<7;x++)
    {
        if(Height[x]<6)
        {
            Feld1 |= 1i64 << (41-(x*6+Height[x]++));
            Spielbaum2(instance+1, ZKey^zufall[0][x*6-1+Height[x]]);
            Feld1 &= ~(1i64<<(41-(--Height[x]+x*6)));
        }
        if(instance==1)
        {
            Label3->Caption=x;
            Form1->Refresh();
        }
    }
    return;
}

void TForm1::Spielbaum2(int instance, unsigned __int64 ZKey)
{
    //if(instance==4)
    	//ProgressBar1->StepIt();
    if(SpielBeenden2())
        return;

    if(instance==maxinstance)
    {
        //if(!BereitsVorhanden(Feld1, Feld2))
        long zaehler = long(ZKey % HASHSIZE);
        {
            a[zaehler]=Feld1;
            b[zaehler]=Feld2;
        }
        return;
    }

    int x;
    for(x=0;x<7;x++)
    {
        if(Height[x]<6)
        {
            Feld2 |= 1i64 << (41-(x*6+Height[x]++));
            Spielbaum1(instance+1, ZKey^zufall[1][x*6-1+Height[x]]);//nächste Instanz des Spielbaums wird gestartet(eine Ebene tiefer)
            Feld2 &= ~(1i64<< (41-(--Height[x]+x*6)));
        }
    }
    return;
}

void TForm1::WurzelMethode(int instance, unsigned __int64 ZKey)
{
    int x;
    for(x=0;x<7;x++)
    {
        if(Height[x]<6)
        {
        	Feld2 |= 1i64 << (41-(x*6+Height[x]++));
            Spielbaum1(instance+1, ZKey^zufall[1][x*6-1+Height[x]]);
            Feld2 &= ~(1i64<< (41-(--Height[x]+x*6)));
            Label2->Caption=x;
            Form1->Refresh();
        }
    }
    return;
}

void TForm1::ZeitmessungStop(void)
{
	/*Nach der Berechnung wird die Zeit gestoppt. Bei der Berechnung wurde die Startzeit
    ermittelt. Es wird die Startzeit von der Stop-Zeit abgezogen und das Ergebnis in MSec
    ausgegeben!!!*/
	Word h, min, sec, msec;
    TDateTime Stopzeit,Differenz;
	Stopzeit=Time();
    Differenz=Stopzeit-Startzeit;
    DecodeTime(Differenz, h, min, sec, msec);
    Label1->Caption= "Zeit: "+(AnsiString)(h*3600+min*60+sec)+" Sec";
    Label1->Caption= Label1->Caption+ " Msec: "+(AnsiString)(msec);
}

Boolean TForm1::BereitsVorhanden(__int64 stellung1, __int64 stellung2)
{
	__int64 i;
    for(i=0;i<HASHSIZE;i++)
    {
    	if(a[i]==-1i64)
        	break;
        if(a[i]==stellung1 && b[i]==stellung2)
            return true;
    }
    return false;
}

void TForm1::InDatei(void)
{
	__int64 i;
    AnsiString pfad1="L:\\1.txt";
    ofstream file1;
    file1.open(pfad1.c_str(), ios::out);
    for(i=0;i<HASHSIZE;i++)
    {
    	if(a[i]!=-1i64)
        {
            file1 << a[i] << ',';
            file1 << b[i];
            file1 << '\n';
        }
	}
    file1.close();
}

void __fastcall TForm1::Button1Click(TObject *Sender)
{
    Startzeit=Time();
    WurzelMethode(0, 0i64);
    ZeitmessungStop();
    InDatei();
}
//---------------------------------------------------------------------------
