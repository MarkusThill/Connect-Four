Boolean SpielBeenden1(void)
{
	const __int64 x = ~Feld1;
	int y = x;
    switch(Hoehe[3])
    {
		case 0:
			if(!(x & 0x20820000000i64 &&
			x & 0x820020000i64 &&
			y & 0x20020800 &&
			y & 0x20820 &&
			y & 0x10204 &&
			x & 0x4210000000i64)) return true;
            break;
        case 1:
			if(!(y & 0x20008100 &&
			y & 0x10410 &&
			x & 0x10410000000i64 &&
			x & 0x2108000000i64 &&
			x & 0x108020000i64 &&
			y & 0x10010400 &&
			y & 0x8102 &&
			x & 0x410010000i64)) return true;
            break;
		case 2:
			if(!(x & 0x810004000i64 &&
			y & 0x4010800 &&
			x & 0x8208000000i64 &&
			x & 0x208008000i64 &&
			y & 0x8008200 &&
			y & 0x8208 &&
			y & 0x84010000 &&
			y & 0x10004080 &&
			y & 0x4081 &&
			x & 0x1084000000i64)) return true;
            break;
        case 3:
			if(!(x & 0x20408000000i64 &&
			y & 0x8420 &&
			y & 0xE00000 &&
			x & 0x4104000000i64 &&
			y & 0x2008400 &&
			x & 0x408002000i64 &&
			y & 0x4004100 &&
			x & 0x104004000i64 &&
			y & 0x42008000 &&
			y & 0x8002040 &&
			y & 0x4104)) return true;
            break;
        case 4:
			if(!(x & 0x10204000000i64 &&
			y & 0x700000 &&
			y & 0x4210 &&
			x & 0x204001000i64 &&
			x & 0x2082000000i64 &&
			y & 0x1004200 &&
			y & 0x82002000 &&
			y & 0x2002080 &&
			y & 0x2082)) return true;
			break;
        case 5:
			if(!(y & 0x380000 &&
			x & 0x8102000000i64 &&
			y & 0x2108 &&
			x & 0x1041000000i64 &&
			y & 0x41001000 &&
			y & 0x1001040 &&
			y & 0x1041)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[4])
    {
        case 0:
			if(!(x & 0x108400000i64 &&
			y & 0x800820 &&
			y & 0x20800800 &&
			x & 0x820800000i64)) return true;
            break;
        case 1:
			if(!(y & 0x800204 &&
			y & 0x4200800 &&
			y & 0x400410 &&
			y & 0x10400400 &&
			x & 0x410400000i64 &&
			y & 0x84200000)) return true;
			break;
        case 2:
			if(!(y & 0x100420 &&
			y & 0x20400100 &&
			y & 0x8200200 &&
			x & 0x208200000i64 &&
			y & 0x200208 &&
			y & 0x2100400 &&
			y & 0x400102 &&
			y & 0x42100000)) return true;
            break;
        case 3:
			if(!(x & 0x810200000i64 &&
			y & 0x80210 &&
			y & 0x38000 &&
			y & 0x4100100 &&
			x & 0x104100000i64 &&
			y & 0x10200080 &&
			y & 0x1080200 &&
			y & 0x100104 &&
			y & 0x200081)) return true;
            break;
        case 4:
			if(!(x & 0x408100000i64 &&
			y & 0x40108 &&
			y & 0x1C000 &&
			y & 0x2080080 &&
			y & 0x82080000 &&
			y & 0x8100040 &&
			y & 0x80082)) return true;
            break;
        case 5:
			if(!(x & 0x204080000i64 &&
			y & 0xE000 &&
			y & 0x1040040 &&
			y & 0x41040000 &&
			y & 0x40041)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[2])
    {
        case 0:
			if(!(y & 0x408100 &&
			x & 0x20800800000i64 &&
			y & 0x820800 &&
			x & 0x800820000i64)) return true;
            break;
        case 1:
			if(!(x & 0x4200800000i64 &&
			x & 0x10400400000i64 &&
			x & 0x800204000i64 &&
			y & 0x410400 &&
			x & 0x400410000i64 &&
			y & 0x204080)) return true;
            break;
        case 2:
			if(!(x & 0x100420000i64 &&
			x & 0x8200200000i64 &&
			x & 0x20400100000i64 &&
			x & 0x200208000i64 &&
			y & 0x208200 &&
			x & 0x400102000i64 &&
			x & 0x2100400000i64 &&
			y & 0x102040)) return true;
            break;
		case 3:
			if(!(y & 0x210800 &&
			x & 0x10200080000i64 &&
			x & 0x4100100000i64 &&
			x & 0x100104000i64 &&
			x & 0x200081000i64 &&
			y & 0x80210000 &&
			y & 0x38000000 &&
			y & 0x104100 &&
			x & 0x1080200000i64)) return true;
            break;
        case 4:
			if(!(y & 0x108400 &&
			x & 0x8100040000i64 &&
			y & 0x80082000 &&
			y & 0x40108000 &&
			y & 0x82080 &&
			y & 0x1C000000 &&
			x & 0x2080080000i64)) return true;
            break;
        case 5:
			if(!(y & 0x84200 &&
			y & 0xE000000 &&
			y & 0x40041000 &&
			y & 0x41040 &&
			x & 0x1040040000i64)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[5])
    {
        case 0:
			if(!(y & 0x4210000 &&
			y & 0x20820000 &&
			y & 0x820020)) return true;
			break;
        case 1:
			if(!(y & 0x10410000 &&
			y & 0x410010 &&
			y & 0x108020 &&
			y & 0x2108000)) return true;
            break;
        case 2:
			if(!(y & 0x8208000 &&
			y & 0x208008 &&
			y & 0x84010 &&
			y & 0x810004 &&
			y & 0x1084000)) return true;
            break;
        case 3:
			if(!(y & 0x20408000 &&
			y & 0x4104000 &&
			y & 0xE00 &&
			y & 0x104004 &&
			y & 0x42008 &&
			y & 0x408002)) return true;
            break;
        case 4:
			if(!(y & 0x10204000 &&
			y & 0x2082000 &&
			y & 0x700 &&
			y & 0x82002 &&
			y & 0x204001)) return true;
            break;
        case 5:
			if(!(y & 0x8102000 &&
			y & 0x1041000 &&
			y & 0x380 &&
			y & 0x41001)) return true;
            break;
        default:
            break;
    }
     switch(Hoehe[1])
    {
        case 0:
			if(!(y & 0x10204000 &&
			y & 0x20820000 &&
			x & 0x20020800000i64)) return true;
            break;
        case 1:
			if(!(x & 0x10010400000i64 &&
			x & 0x20008100000i64 &&
			y & 0x10410000 &&
			y & 0x8102000)) return true;
            break;
        case 2:
			if(!(x & 0x8008200000i64 &&
			x & 0x4010800000i64 &&
			y & 0x8208000 &&
			x & 0x10004080000i64 &&
			y & 0x4081000)) return true;
            break;
        case 3:
			if(!(y & 0x8420000 &&
			x & 0xE00000000i64 &&
			y & 0x4104000 &&
			x & 0x4004100000i64 &&
			x & 0x8002040000i64 &&
			x & 0x2008400000i64)) return true;
            break;
        case 4:
			if(!(y & 0x4210000 &&
			y & 0x2082000 &&
			x & 0x700000000i64 &&
			x & 0x2002080000i64 &&
			x & 0x1004200000i64)) return true;
            break;
        case 5:
			if(!(y & 0x2108000 &&
			y & 0x1041000 &&
			x & 0x380000000i64 &&
			x & 0x1001040000i64)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[6])
    {
        case 0:
			if(!(y & 0x108400 &&
			y & 0x820800)) return true;
            break;
		case 1:
			if(!(y & 0x410400 &&
			y & 0x84200)) return true;
            break;
        case 2:
			if(!(y & 0x208200 &&
			y & 0x42100)) return true;
            break;
		case 3:
			if(!(y & 0x810200 &&
			y & 0x38 &&
			y & 0x104100)) return true;
            break;
        case 4:
			if(!(y & 0x408100 &&
			y & 0x1C &&
			y & 0x82080)) return true;
            break;
        case 5:
			if(!(y & 0x204080 &&
			y & 0xE &&
			y & 0x41040)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[0])
    {
        case 0:
			if(!(x & 0x408100000i64 &&
			x & 0x820800000i64)) return true;
            break;
        case 1:
			if(!(x & 0x410400000i64 &&
			x & 0x204080000i64)) return true;
            break;
        case 2:
			if(!(x & 0x208200000i64 &&
			x & 0x102040000i64)) return true;
            break;
        case 3:
			if(!(x & 0x38000000000i64 &&
			x & 0x210800000i64 &&
			x & 0x104100000i64)) return true;
            break;
        case 4:
			if(!(x & 0x108400000i64 &&
			x & 0x1C000000000i64 &&
			y & 0x82080000)) return true;
            break;
		case 5:
			if(!(y & 0x84200000 &&
			x & 0xE000000000i64 &&
			y & 0x41040000)) return true;
            break;
        default:
            break;
    }
    return false;
}

Boolean SpielBeenden2(void)
{
	const __int64 x = ~Feld2;
	int y = x;
    switch(Hoehe[3])
    {
		case 0:
			if(!(x & 0x20820000000i64 &&
			x & 0x820020000i64 &&
			y & 0x20020800 &&
			y & 0x20820 &&
			y & 0x10204 &&
			x & 0x4210000000i64)) return true;
            break;
        case 1:
			if(!(y & 0x20008100 &&
			y & 0x10410 &&
			x & 0x10410000000i64 &&
			x & 0x2108000000i64 &&
			x & 0x108020000i64 &&
			y & 0x10010400 &&
			y & 0x8102 &&
			x & 0x410010000i64)) return true;
            break;
		case 2:
			if(!(x & 0x810004000i64 &&
			y & 0x4010800 &&
			x & 0x8208000000i64 &&
			x & 0x208008000i64 &&
			y & 0x8008200 &&
			y & 0x8208 &&
			y & 0x84010000 &&
			y & 0x10004080 &&
			y & 0x4081 &&
			x & 0x1084000000i64)) return true;
            break;
        case 3:
			if(!(x & 0x20408000000i64 &&
			y & 0x8420 &&
			y & 0xE00000 &&
			x & 0x4104000000i64 &&
			y & 0x2008400 &&
			x & 0x408002000i64 &&
			y & 0x4004100 &&
			x & 0x104004000i64 &&
			y & 0x42008000 &&
			y & 0x8002040 &&
			y & 0x4104)) return true;
            break;
        case 4:
			if(!(x & 0x10204000000i64 &&
			y & 0x700000 &&
			y & 0x4210 &&
			x & 0x204001000i64 &&
			x & 0x2082000000i64 &&
			y & 0x1004200 &&
			y & 0x82002000 &&
			y & 0x2002080 &&
			y & 0x2082)) return true;
			break;
        case 5:
			if(!(y & 0x380000 &&
			x & 0x8102000000i64 &&
			y & 0x2108 &&
			x & 0x1041000000i64 &&
			y & 0x41001000 &&
			y & 0x1001040 &&
			y & 0x1041)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[4])
    {
        case 0:
			if(!(x & 0x108400000i64 &&
			y & 0x800820 &&
			y & 0x20800800 &&
			x & 0x820800000i64)) return true;
            break;
        case 1:
			if(!(y & 0x800204 &&
			y & 0x4200800 &&
			y & 0x400410 &&
			y & 0x10400400 &&
			x & 0x410400000i64 &&
			y & 0x84200000)) return true;
			break;
        case 2:
			if(!(y & 0x100420 &&
			y & 0x20400100 &&
			y & 0x8200200 &&
			x & 0x208200000i64 &&
			y & 0x200208 &&
			y & 0x2100400 &&
			y & 0x400102 &&
			y & 0x42100000)) return true;
            break;
        case 3:
			if(!(x & 0x810200000i64 &&
			y & 0x80210 &&
			y & 0x38000 &&
			y & 0x4100100 &&
			x & 0x104100000i64 &&
			y & 0x10200080 &&
			y & 0x1080200 &&
			y & 0x100104 &&
			y & 0x200081)) return true;
            break;
        case 4:
			if(!(x & 0x408100000i64 &&
			y & 0x40108 &&
			y & 0x1C000 &&
			y & 0x2080080 &&
			y & 0x82080000 &&
			y & 0x8100040 &&
			y & 0x80082)) return true;
            break;
        case 5:
			if(!(x & 0x204080000i64 &&
			y & 0xE000 &&
			y & 0x1040040 &&
			y & 0x41040000 &&
			y & 0x40041)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[2])
    {
        case 0:
			if(!(y & 0x408100 &&
			x & 0x20800800000i64 &&
			y & 0x820800 &&
			x & 0x800820000i64)) return true;
            break;
        case 1:
			if(!(x & 0x4200800000i64 &&
			x & 0x10400400000i64 &&
			x & 0x800204000i64 &&
			y & 0x410400 &&
			x & 0x400410000i64 &&
			y & 0x204080)) return true;
            break;
        case 2:
			if(!(x & 0x100420000i64 &&
			x & 0x8200200000i64 &&
			x & 0x20400100000i64 &&
			x & 0x200208000i64 &&
			y & 0x208200 &&
			x & 0x400102000i64 &&
			x & 0x2100400000i64 &&
			y & 0x102040)) return true;
            break;
		case 3:
			if(!(y & 0x210800 &&
			x & 0x10200080000i64 &&
			x & 0x4100100000i64 &&
			x & 0x100104000i64 &&
			x & 0x200081000i64 &&
			y & 0x80210000 &&
			y & 0x38000000 &&
			y & 0x104100 &&
			x & 0x1080200000i64)) return true;
            break;
        case 4:
			if(!(y & 0x108400 &&
			x & 0x8100040000i64 &&
			y & 0x80082000 &&
			y & 0x40108000 &&
			y & 0x82080 &&
			y & 0x1C000000 &&
			x & 0x2080080000i64)) return true;
            break;
        case 5:
			if(!(y & 0x84200 &&
			y & 0xE000000 &&
			y & 0x40041000 &&
			y & 0x41040 &&
			x & 0x1040040000i64)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[5])
    {
        case 0:
			if(!(y & 0x4210000 &&
			y & 0x20820000 &&
			y & 0x820020)) return true;
			break;
        case 1:
			if(!(y & 0x10410000 &&
			y & 0x410010 &&
			y & 0x108020 &&
			y & 0x2108000)) return true;
            break;
        case 2:
			if(!(y & 0x8208000 &&
			y & 0x208008 &&
			y & 0x84010 &&
			y & 0x810004 &&
			y & 0x1084000)) return true;
            break;
        case 3:
			if(!(y & 0x20408000 &&
			y & 0x4104000 &&
			y & 0xE00 &&
			y & 0x104004 &&
			y & 0x42008 &&
			y & 0x408002)) return true;
            break;
        case 4:
			if(!(y & 0x10204000 &&
			y & 0x2082000 &&
			y & 0x700 &&
			y & 0x82002 &&
			y & 0x204001)) return true;
            break;
        case 5:
			if(!(y & 0x8102000 &&
			y & 0x1041000 &&
			y & 0x380 &&
			y & 0x41001)) return true;
            break;
        default:
            break;
    }
     switch(Hoehe[1])
    {
        case 0:
			if(!(y & 0x10204000 &&
			y & 0x20820000 &&
			x & 0x20020800000i64)) return true;
            break;
        case 1:
			if(!(x & 0x10010400000i64 &&
			x & 0x20008100000i64 &&
			y & 0x10410000 &&
			y & 0x8102000)) return true;
            break;
        case 2:
			if(!(x & 0x8008200000i64 &&
			x & 0x4010800000i64 &&
			y & 0x8208000 &&
			x & 0x10004080000i64 &&
			y & 0x4081000)) return true;
            break;
        case 3:
			if(!(y & 0x8420000 &&
			x & 0xE00000000i64 &&
			y & 0x4104000 &&
			x & 0x4004100000i64 &&
			x & 0x8002040000i64 &&
			x & 0x2008400000i64)) return true;
            break;
        case 4:
			if(!(y & 0x4210000 &&
			y & 0x2082000 &&
			x & 0x700000000i64 &&
			x & 0x2002080000i64 &&
			x & 0x1004200000i64)) return true;
            break;
        case 5:
			if(!(y & 0x2108000 &&
			y & 0x1041000 &&
			x & 0x380000000i64 &&
			x & 0x1001040000i64)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[6])
    {
        case 0:
			if(!(y & 0x108400 &&
			y & 0x820800)) return true;
            break;
		case 1:
			if(!(y & 0x410400 &&
			y & 0x84200)) return true;
            break;
        case 2:
			if(!(y & 0x208200 &&
			y & 0x42100)) return true;
            break;
		case 3:
			if(!(y & 0x810200 &&
			y & 0x38 &&
			y & 0x104100)) return true;
            break;
        case 4:
			if(!(y & 0x408100 &&
			y & 0x1C &&
			y & 0x82080)) return true;
            break;
        case 5:
			if(!(y & 0x204080 &&
			y & 0xE &&
			y & 0x41040)) return true;
            break;
        default:
            break;
    }
    switch(Hoehe[0])
    {
        case 0:
			if(!(x & 0x408100000i64 &&
			x & 0x820800000i64)) return true;
            break;
        case 1:
			if(!(x & 0x410400000i64 &&
			x & 0x204080000i64)) return true;
            break;
        case 2:
			if(!(x & 0x208200000i64 &&
			x & 0x102040000i64)) return true;
            break;
        case 3:
			if(!(x & 0x38000000000i64 &&
			x & 0x210800000i64 &&
			x & 0x104100000i64)) return true;
            break;
        case 4:
			if(!(x & 0x108400000i64 &&
			x & 0x1C000000000i64 &&
			y & 0x82080000)) return true;
            break;
		case 5:
			if(!(y & 0x84200000 &&
			x & 0xE000000000i64 &&
			y & 0x41040000)) return true;
            break;
        default:
            break;
    }
    return false;
}


int SpielBeendenStellung(Boolean spieler)
{
    /*Überprüft, ob der übergebene Spieler gewinnen könnte. Wenn er gewinnen
    kann, dann wird die spalte, in die er setzen müsste, um zu gewinnnen, zurückgegeben,
    ansonsten (-1).*/
	int x;
    for(x=0;x<7;x++)
    {
        if(Hoehe[x]<6)
        {
            if(spieler==SPIELER1)
            {
                if(Gewinnstellung1(x, Hoehe[x]))
        	        return x;
            }
            else if(spieler==SPIELER2)
            {
				if(Gewinnstellung2(x, Hoehe[x]))
                    return x;
            }
        }
    }
    return (-1); //Wenn keine Siegstellung gefunden wurde
}

int SpielBeendenStellungD(Boolean spieler, int *arr)
{
	int x, j=0;
    for(x=0;x<7;x++)
    {
        if(Hoehe[x]<6)
        {
            if(spieler==SPIELER1)
            {
				if(Gewinnstellung1(x, Hoehe[x])) {
					*arr = x;
					j++;
				}
            }
            else if(spieler==SPIELER2)
            {
				if(Gewinnstellung2(x, Hoehe[x])) {
					*arr = x;
					j++;
                }
            }
        }
    }
    return j;
}
