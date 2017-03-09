package bet.belleepoquetech.radarufpa;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private EditText mInputMensagem;
    private Button mBtnEnviar;
    private ListView mListaMensagens;
    private ArrayAdapter<String> mAdapter;
    private Socket mSocket;
    private String url = "http://10.0.2.2";

    public static ChatFragment newInstance() {
        ChatFragment fragment = new ChatFragment();
        return fragment;
    }

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        mInputMensagem = (EditText) rootView.findViewById(R.id.input_mensagem);
        mBtnEnviar = (Button) rootView.findViewById(R.id.btn_enviar);
        mListaMensagens = (ListView) rootView.findViewById(R.id.lista);

        mAdapter = new ArrayAdapter<>(rootView.getContext(), android.R.layout.simple_list_item_1);

        mListaMensagens.setAdapter(mAdapter);
        mBtnEnviar.setOnClickListener(this);

        try {
            mSocket = IO.socket(url+":81");
        } catch (URISyntaxException e) {
            this.getActivity().finish();
            return rootView;
        }

        mSocket.on("mensagem_cliente", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                if (args.length > 0) {
                    // Note a necessidade de usar o método runOnUiThread pois este código é
                    // executado numa thread separada, então precisamos rodar o código da UI
                    // na thread adequada
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add((String) args[0]);
                            mAdapter.notifyDataSetChanged();

                            // Apenas faz um scroll para o novo item da lista
                            mListaMensagens.smoothScrollToPosition(mAdapter.getCount() - 1);
                        }
                    });
                }
            }
        });

        mSocket.connect();


        return rootView;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_enviar:enviar();break;
        }
    }

    public void enviar(){
        if (!mInputMensagem.getText().toString().equals("")) {
            mSocket.emit("mensagem_servidor", mInputMensagem.getText());
            mInputMensagem.setText("");
        }
    }
}
