package notNeeded;

import java.awt.*;
import java.awt.event.*;

public class MyImgShow extends Frame
{
	private static final long serialVersionUID = 1L;

public static void main( String[] args )
  {
    if( 1 > args.length )
      System.out.println( "Usage:\njava MyImgShow <ImgFile>\nE.g.:\njava MyImgShow x.png" );
    else
      new MyImgShow( args[0] );
  }

  MyImgShow( String sFile )
  {
    super( sFile );
    setSize( 100, 100 );
    setVisible( true );
    //TODO: add( new ImgShowComponent( sFile ) );
    pack();
    addWindowListener(
      new WindowAdapter() {
        public void windowClosing( WindowEvent ev ) {
          dispose();
          System.exit( 0 ); } } );
  }
}
