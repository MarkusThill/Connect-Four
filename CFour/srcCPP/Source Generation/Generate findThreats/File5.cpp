//---------------------------------------------------------------------------

#include <vcl.h>
#include <fstream.h>
#pragma hdrstop

//---------------------------------------------------------------------------

__int64 viererreihen[49]=
{
	0x1041040000i64,
	0x41041000i64,
	0x1041040i64,
	0x41041i64,
	0x2082080000i64,
	0x82082000i64,
	0x2082080i64,
	0x82082i64,
	0x1084200000i64,
	0x42108000i64,
	0x1084200i64,
	0x42108i64,
	0x8102040000i64,
	0x204081000i64,
	0x8102040i64,
	0x204081i64,
	0x4104100000i64,
	0x104104000i64,
	0x4104100i64,
	0x104104i64,
	0x2108400000i64,
	0x84210000i64,
	0x2108400i64,
	0x84210i64,
	0x10204080000i64,
	0x408102000i64,
	0x10204080i64,
	0x408102i64,
	0x8208200000i64,
	0x208208000i64,
	0x8208200i64,
	0x208208i64,
	0x4210800000i64,
	0x108420000i64,
	0x4210800i64,
	0x108420i64,
	0x20408100000i64,
	0x810204000i64,
	0x20408100i64,
	0x810204i64,
	0x10410400000i64,
	0x410410000i64,
	0x10410400i64,
	0x410410i64,
	0x20820800000i64,
	0x820820000i64,
	0x20820800i64,
	0x820820i64
};

const unsigned __int64 Feldwert[7][6]=
			{2199023255552i64,
			1099511627776i64,
			549755813888i64,
			274877906944i64,
			137438953472i64,
			68719476736i64,
			34359738368i64,
			17179869184i64,
			8589934592i64,
			4294967296i64,
			2147483648i64,
			1073741824i64,
			536870912i64,
			268435456i64,
			134217728i64,
			67108864i64,
			33554432i64,
			16777216i64,
			8388608i64,
			4194304i64,
			2097152i64,
			1048576i64,
			524288i64,
			262144i64,
			131072i64,
			65536i64,
			32768i64,
			16384i64,
			8192i64,
			4096i64,
			2048i64,
			1024i64,
			512i64,
			256i64,
			128i64,
			64i64,
			32i64,
			16i64,
			8i64,
			4i64,
			2i64,
			1i64};


void fgh(void);
bool WeitereDrohung(__int64 feld, int *x1, int *y1, int *x2, int *y2);
AnsiString ToHex(__int64 wert);

#pragma argsused
int main(int argc, char* argv[])
{
fgh();
	return 0;
}
//---------------------------------------------------------------------------

void fgh(void)
{
	int i,j,k, counter=0,l,m;
	__int64 array[30];
	ofstream datei;
	datei.open("C:\\10\\find.txt", ios::out);
	datei << "short int FindThreats1(void)\n{\n\n";
	for(l=0;l<7;l++)
	{
		datei << "\tswitch(Hoehe[" << l << "])\n\t{\n";
		for(m=0;m<6;m++)
		{
			datei << "\t\tcase " << m <<":\n";
			for(k=0;k<30;k++)
				array[k]=-1i64;
			int r=0;
			for(k=0;k<49;k++)
				for(i=0;i<7;i++)
					for(j=0;j<6;j++)
						if( (Feldwert[i][j] & viererreihen[k]))
						{
							__int64 dreierreihe= viererreihen[k] & (~Feldwert[i][j]);
							int spalte1=-1, spalte2=-1, reihe1=-1, reihe2=-1;
							if(!WeitereDrohung(dreierreihe, &spalte1, &reihe1, &spalte2, &reihe2))
							{
								spalte1=i;
								reihe1=j;
							}
							if(Feldwert[l][m] & dreierreihe)
							{
								__int64 zweierreihe = dreierreihe & (~Feldwert[l][m]);
								if(reihe1>0 && reihe2>0 && spalte1>=0 && spalte2>=0)
								{
									counter++;
									int u;
									for(u=0;u<30;u++)
										if(array[u]==zweierreihe)
											goto eins;
									array[r++]=zweierreihe;
									AnsiString wert=ToHex(zweierreihe);
									datei << "\t\t\tif((Feld1 & 0x"<< wert.c_str() << "i64) == 0x"<<wert.c_str()<<"i64 && (Hoehe[" <<spalte1 <<"]<" <<reihe1 << " || Hoehe[" << spalte2 <<"]<" << reihe2 << ")) return "<< l <<";\n";
								}
								else if(reihe1>0 && spalte1>=0)
								{
									counter++;
									int u;
									for(u=0;u<30;u++)
										if(array[u]==zweierreihe)
											goto eins;
									array[r++]=zweierreihe;
									AnsiString wert=ToHex(zweierreihe);
									datei << "\t\t\tif((Feld1 & 0x"<< wert.c_str() << "i64) == 0x"<<wert.c_str()<<"i64 && Hoehe[" <<spalte1 <<"]<" <<reihe1 << ") return "<< l <<";\n";
								}
								else if(reihe2>0 && spalte2>=0)
								{
									counter++;
									int u;
									for(u=0;u<30;u++)
										if(array[u]==zweierreihe)
											goto eins;
									array[r++]=zweierreihe;
									AnsiString wert=ToHex(zweierreihe);
									datei << "\t\t\tif((Feld1 & 0x"<< wert.c_str() << "i64) == 0x"<<wert.c_str()<<"i64 && Hoehe[" <<spalte2 <<"]<" <<reihe2 << ") return "<< l <<";\n";
								} 
eins:
							}

					}
			datei << "\t\t\tbreak;\n";
		}
		datei << "\t\tdefault:\n\t\t\tbreak;\n\t}\n";
	}
	datei << "\treturn (-1);\n}";
}

bool WeitereDrohung(__int64 feld, int *x1, int *y1, int *x2, int *y2)
{
	int i,j;
	for(i=0;i<7;i++)
		for(j=0;j<6;j++)
		{
			if(Feldwert[i][j] & feld)
			{
				if(i>=4 || i==0) //es kann nicht auf 2 Seiten sein
					return false;

				if( (Feldwert[i+1][j] & feld) && (Feldwert[i+2][j] & feld) ) //Horizontal
				{
					*x1=i-1; *x2=i+3; *y1=j; *y2=j;
					return true;
				}

				if(j<3 && j>0) //Diagonal hoch
					if( (Feldwert[i+1][j+1] & feld) && (Feldwert[i+2][j+2] & feld) )
					{
						*x1=i-1; *x2=i+3; *y1=j-1; *y2=j+3;
						return true;
					}

				if(j>2 && j<5) //Diagonal runter
					if( (Feldwert[i+1][j-1] & feld) && (Feldwert[i+2][j-2] & feld))
                    {
						*x1=i-1; *x2=i+3; *y1=j+1; *y2=j-3;
						return true;
					}

				return false;
            }
		}
	return false;
}

AnsiString ToHex(__int64 wert)
{
	AnsiString rueck;
    rueck.SetLength(0);
    int x = 15;
    __int64 verschiebe, temp, nibble;
    Boolean trotzdem=false;
    for(int i=15;i>=0;i--)
    {
        verschiebe= ((__int64)x) << (i*4i64);
        temp = wert & verschiebe;
        nibble = temp >> (i*4i64);
        if(nibble>0 || trotzdem)
        {
            rueck.SetLength(rueck.Length()+1);
            trotzdem=true;
            if(nibble<10)
               rueck[rueck.Length()]=(nibble+48);
            else
                rueck[rueck.Length()]=(nibble+55);
        }

    }
    return rueck;
}

