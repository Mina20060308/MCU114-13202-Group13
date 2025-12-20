package com.example.notes;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.notes.database.UserRepository;

public class RegisterFragment extends Fragment {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private UserRepository userRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etEmail = view.findViewById(R.id.etEmailRegister);
        etPassword = view.findViewById(R.id.etPasswordRegister);
        etConfirmPassword = view.findViewById(R.id.etConfirmPasswordRegister);
        btnRegister = view.findViewById(R.id.btnRegister);

        userRepo = new UserRepository(requireContext());

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirm = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirm)) {
                Toast.makeText(getContext(), "請完整填寫欄位", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(getContext(), "密碼不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            if (userRepo.isEmailExists(email)) {
                Toast.makeText(getContext(), "此 Email 已被註冊", Toast.LENGTH_SHORT).show();
                return;
            }

            userRepo.registerUser(email, password);

            Toast.makeText(getContext(), "註冊成功！請登入", Toast.LENGTH_SHORT).show();

            // 回登入畫面
            requireActivity().getSupportFragmentManager().popBackStack();
            requireActivity().findViewById(R.id.login_form).setVisibility(View.VISIBLE);
        });

        return view;
    }
}
