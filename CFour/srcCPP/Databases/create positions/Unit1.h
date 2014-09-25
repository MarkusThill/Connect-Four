//---------------------------------------------------------------------------

#ifndef Unit1H
#define Unit1H
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
#include <ComCtrls.hpp>
#include <fstream.h>
//---------------------------------------------------------------------------
class TForm1 : public TForm
{
__published:	// Von der IDE verwaltete Komponenten
    TProgressBar *ProgressBar1;
    TLabel *Label1;
    TButton *Button1;
    TLabel *Label2;
    TLabel *Label3;
    TEdit *Edit1;
    void __fastcall Button1Click(TObject *Sender);
private:	// Anwender-Deklarationen
        __int64 *a;
        __int64 *b;
        __int64 Feld1;
        __int64 Feld2;
        short int maxinstance;
        short int instancevoll;
        int Height[7];
        long zaehler;
        TDateTime Startzeit;
public:		// Anwender-Deklarationen
        Boolean Gewinnstellung(int x, int y, Boolean spieler);
        Boolean Gewinnstellung1(int x, int y);
        Boolean Gewinnstellung2(int x, int y);
        Boolean SpielBeenden1(void);
        Boolean SpielBeenden2(void);
        Boolean SpielBeenden(Boolean spieler);
        int SpielBeendenStellung(Boolean spieler);
        void Spielbaum1(int instance, unsigned __int64 ZKey);
        void Spielbaum2(int instance, unsigned __int64 ZKey);
        void WurzelMethode(int instance, unsigned __int64 ZKey);
        Boolean BereitsVorhanden(__int64 stellung1, __int64 stellung2);
        inline void SteinSetzen(int x, Boolean spieler);
        inline void SteinLoeschen(int x, Boolean spieler);
        void ZeitmessungStop(void);
        void InDatei(void);
    __fastcall TForm1(TComponent* Owner);
};
//---------------------------------------------------------------------------
extern PACKAGE TForm1 *Form1;
//---------------------------------------------------------------------------
#endif
 