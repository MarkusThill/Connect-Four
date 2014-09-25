//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "Unit1.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TForm1 *Form1;
//---------------------------------------------------------------------------
__fastcall TForm1::TForm1(TComponent* Owner)
    : TForm(Owner)
{
    randomize();
    GenerateZobristKeys();
    Edit1->Text="Datei wurde unter C:\Zobrist gespeichert";
}
//---------------------------------------------------------------------------


void TForm1::GenerateZobristKeys(void)
{
    int i;
    unsigned __int64 ZufallsZahl[2][42];
    ofstream file;
    file.open("C:\\Zobrist.txt", ios::out);
    for(i=0;i<42;i++)
    {
        ZufallsZahl[0][i]= Rand64();
    }
    for(i=0;i<42;i++)
    {
        ZufallsZahl[1][i]= Rand64();
    }
    for(i=0;i<42;i++)
    {
        file << ZufallsZahl[0][i] << '\n';
        file << ZufallsZahl[1][i] << '\n';
    }
    file.close();
}

unsigned __int64 TForm1::Rand64(void)
{
    TDateTime StartZeit;
    __int64 i;
    //for(i=0i64;i<99999999i64;i++)
        //i++;
    return rand() ^ ((unsigned __int64)rand() << 15) ^ ((unsigned __int64)rand() << 30) ^ ((unsigned __int64)rand() << 45) ^ ((unsigned __int64)rand() << 60);
}
