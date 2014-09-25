//---------------------------------------------------------------------------

#include <vcl.h>
#include <fstream.h>
#pragma hdrstop

#include "Unit1.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TForm1 *Form1;

#define TABLESIZE 49999999i64
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
    : TForm(Owner)
{
    TABLE = (HASHEvorlage *)malloc(TABLESIZE * sizeof(HASHEvorlage));
    ResetTable();
}
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


void __fastcall TForm1::Button1Click(TObject *Sender)
{
    ifstream file;
    file.open("C:\\1.txt",ios::in);
    char zeile1[50], *zeile2, *zeile3, *test;
    long i=0;
    short x=0;
    unsigned __int64 ZKey;
    long HashPosition;
    while(i<7443256)
    {
        file >> zeile1;
        zeile2=zeile1;
        zeile3 = strchr(zeile1,',');
        *zeile3++='\0';
        Feld1 = CharToInt64dec(zeile2);
        Feld2 = CharToInt64dec(zeile3);
        ZKey = HeightErmitteln();
        HashPosition = (long)(ZKey%TABLESIZE);
        TABLE[HashPosition].feld1 = Feld1;
        TABLE[HashPosition].feld2 = Feld2;
        i++;
        if(!(i % 10000))
        {
            Label1->Caption = (AnsiString)i;
            Form1->Refresh();
        }
    }
    file.close();
}
//---------------------------------------------------------------------------

__int64 TForm1::CharToInt64dec(char *string)
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
        ret+=(__int64)(string[i]-48i64)*pow(10,potenz++);
    return ret;
}

void TForm1::ResetTable(void)
{
    for(long i=0; i<TABLESIZE;i++)
    {
        TABLE[i].feld1=0i64;
        TABLE[i].feld2=0i64;
    }
}

unsigned __int64 TForm1::HeightErmitteln(void)
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

unsigned __int64 TForm1::ZKeyGespiegelt(void)
{
    int j,m,i, stelle1, stelle2;
    __int64 Feld1Gespiegelt=0i64, Feld2Gespiegelt=0i64;
    for(j=0,m=6;j<7 && m>=0;j++,m--)
        for(i=0;i<6;i++)
        {
            stelle1=41-(m*6+i);
            stelle2=41-(j*6+i);
            if((1i64<<stelle1 & Feld1) == 1i64<<stelle1)
                Feld1Gespiegelt |= 1i64 << stelle2;
            else if((1i64<<stelle1 & Feld2) == 1i64<<stelle1)
                Feld2Gespiegelt |= 1i64 << stelle2;
        }
        Feld1 = Feld1Gespiegelt;
        Feld2 = Feld2Gespiegelt;
    unsigned __int64 ZKey =HeightErmitteln();
    return ZKey;
}

void __fastcall TForm1::Button2Click(TObject *Sender)
{
    /*long i, j;
    for(i=0;i<TABLESIZE;i++)
    {
        if(TABLE[i].feld1==0)
            continue;
        for(j=i+1;j<TABLESIZE;j++)
        {
            if(TABLE[i].feld1 == TABLE[j].feld1 && TABLE[i].feld2 == TABLE[j].feld2)
            {
                TABLE[j].feld1=0i64;
                TABLE[j].feld2=0i64;
                break;
            }
        }
        if(!(i%1000))
        {
            Label1->Caption = i;
            Form1->Refresh();
        }
    } */
    long i;
    unsigned __int64 ZKey;
    long Position;
    for(i=0;i<TABLESIZE;i++)
    {
        if(TABLE[i].feld1==0i64)
            continue;
        Feld1 = TABLE[i].feld1;
        Feld2 = TABLE[i].feld2;
        ZKey = ZKeyGespiegelt();
        Position= (long)(ZKey%TABLESIZE);
        if(TABLE[Position].feld1!=0i64)
        {
            TABLE[Position].feld1 = 0i64;
            TABLE[Position].feld2 = 0i64;
        }
        if(!(i%10000))
        {
            Label1->Caption = i;
            Form1->Refresh();
        }
    }
}
//---------------------------------------------------------------------------
void __fastcall TForm1::Button3Click(TObject *Sender)
{
    ofstream file;
    file.open("C:\\spie.txt", ios::out);
    for(long i=0;i<TABLESIZE;i++)
    {
        if(TABLE[i].feld1!=0)
        {
            file << TABLE[i].feld1 << ',' << TABLE[i].feld2 << '\n';
        }
    }
}
//---------------------------------------------------------------------------

void __fastcall TForm1::Button4Click(TObject *Sender)
{
    long counter=0;
    for(long i=0;i<TABLESIZE;i++)
    {
        if(TABLE[i].feld1!=0)
            counter++;
    }
    Label1->Caption=(AnsiString)counter;
}
//---------------------------------------------------------------------------

