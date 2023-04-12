package es.netmind.banana_invoices.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

import es.netmind.banana_invoices.models.Recibo;
import es.netmind.banana_invoices.models.ReciboInvalido;

public class ReciboSimpleWriter implements ItemWriter<Object> {

    @Override
    public void write(List<? extends Object> list) throws Exception {
        System.out.println("RecWriter write()....:" + list.size());

        Recibo currentRecibo = null;
        ReciboInvalido currentReciboInv = null;
        for (Object item : list) {
            currentRecibo = (Recibo) item;
            if (!currentRecibo.isValido()) {
                currentReciboInv = (ReciboInvalido) item;
                System.out.printf("\t ...writing INVALIDO: %s\n", currentReciboInv);
            }else{
                System.out.printf("\t ...writing VALIDO: %s\n", currentRecibo);
            }
        }
    }
}
