//---------------------------------------------------------------------------

#ifndef Unit1H
#define Unit1H
//---------------------------------------------------------------------------
#include <Classes.hpp>
#include <Controls.hpp>
#include <StdCtrls.hpp>
#include <Forms.hpp>
//---------------------------------------------------------------------------
class TForm1 : public TForm
{
__published:	// Von der IDE verwaltete Komponenten
    TButton *Button1;
    TLabel *Label1;
    TButton *Button2;
    TButton *Button3;
    TButton *Button4;
    void __fastcall Button1Click(TObject *Sender);
    void __fastcall Button2Click(TObject *Sender);
    void __fastcall Button3Click(TObject *Sender);
    void __fastcall Button4Click(TObject *Sender);
private:	// Anwender-Deklarationen
            __int64 Feld1;
            __int64 Feld2;

    struct HASHEvorlage {
    __int64 feld1;
    __int64 feld2;
    }*TABLE;

public:		// Anwender-Deklarationen
        __int64 TForm1::CharToInt64dec(char *string);
        void TForm1::ResetTable(void);
    __fastcall TForm1(TComponent* Owner);
    unsigned __int64 TForm1::ZKeyGespiegelt(void);
    unsigned __int64 TForm1::HeightErmitteln(void);
};
//---------------------------------------------------------------------------
extern PACKAGE TForm1 *Form1;
//---------------------------------------------------------------------------
#endif
