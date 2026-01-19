package com.example.smartport.UserTransportista;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.smartport.R;
import com.example.smartport.UserTransportista.Cargo;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CargoAdapter extends RecyclerView.Adapter<CargoAdapter.CargoViewHolder> {

    private List<Cargo> cargoList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public CargoAdapter(List<Cargo> cargoList, Context context) {
        this.cargoList = cargoList;
        this.context = context;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public CargoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cargo, parent, false);
        return new CargoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CargoViewHolder holder, int position) {
        Cargo cargo = cargoList.get(position);

        // 加载图片
        if (cargo.getImagenUrl() != null && !cargo.getImagenUrl().isEmpty()) {
            Glide.with(context)
                    .load(cargo.getImagenUrl())
                    .placeholder(R.drawable.placeholder_cargo)
                    .into(holder.imgCargo);
        } else {
            holder.imgCargo.setImageResource(R.drawable.placeholder_cargo);
        }

        // 设置数据
        holder.tvTipo.setText(cargo.getTipo());
        holder.tvPeso.setText(String.format("Peso: %.2f kg", cargo.getPesoTotal()));

        // 设置状态
        if (cargo.isEnviado()) {
            holder.tvEstado.setText("Enviado");
            holder.tvEstado.setBackgroundColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvEstado.setText("Pendiente");
            holder.tvEstado.setBackgroundColor(context.getResources().getColor(R.color.orange));
        }

        // ===== 安全审核状态 =====
        if ("PENDING".equalsIgnoreCase(cargo.getSecurityStatus())) {
            holder.tvSecurity.setVisibility(View.VISIBLE);
            holder.tvSecurity.setText("Sin revisar");
        } else {
            holder.tvSecurity.setVisibility(View.GONE);
        }


        // 设置日期
        if (cargo.getFechaCreacion() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fecha = dateFormat.format(cargo.getFechaCreacion().toDate());
            holder.tvFecha.setText("Fecha: " + fecha);
        } else {
            holder.tvFecha.setText("Fecha: No disponible");
        }

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            showCargoDetailDialog(cargo);

        });
    }

    private void showCargoDetailDialog(Cargo cargo) {
        final Dialog detailDialog = new Dialog(context);
        detailDialog.setContentView(R.layout.dialog_cargo_detail);
        detailDialog.setCancelable(true);

        // 设置弹窗宽度为屏幕宽度的90%，高度自适应
        Window window = detailDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (getScreenWidth() * 0.90); // 屏幕宽度的90%
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.CENTER;
            window.setAttributes(layoutParams);

            // 可选：设置弹窗背景为圆角
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        // 初始化视图
        ImageView ivDetailImage = detailDialog.findViewById(R.id.ivDetailImage);
        TextView tvDetailTipo = detailDialog.findViewById(R.id.tvDetailTipo);
        TextView tvDetailPeso = detailDialog.findViewById(R.id.tvDetailPeso);
        TextView tvDetailEstado = detailDialog.findViewById(R.id.tvDetailEstado);
        TextView tvDetailFecha = detailDialog.findViewById(R.id.tvDetailFecha);
        TextView tvDetailId = detailDialog.findViewById(R.id.tvDetailId);
        TextView tvDetailDescripcion = detailDialog.findViewById(R.id.tvDetailDescripcion);
        Button btnCloseDetail = detailDialog.findViewById(R.id.btnCloseDetail);
        Button btnEditCargo = detailDialog.findViewById(R.id.btnEditCargo);


        // 设置货物数据
        // 图片
        if (cargo.getImagenUrl() != null && !cargo.getImagenUrl().isEmpty()) {
            Glide.with(context)
                    .load(cargo.getImagenUrl())
                    .placeholder(R.drawable.placeholder_cargo)
                    .error(R.drawable.placeholder_cargo)
                    .into(ivDetailImage);
        }

        // 基本信息
        tvDetailTipo.setText(cargo.getTipo());
        tvDetailPeso.setText(String.format("%.2f kg", cargo.getPesoTotal()));

        // 状态
        if (cargo.isEnviado()) {
            tvDetailEstado.setText("Enviado");
            tvDetailEstado.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            tvDetailEstado.setText("Pendiente");
            tvDetailEstado.setTextColor(context.getResources().getColor(R.color.orange));
        }

        // 日期
        if (cargo.getFechaCreacion() != null) {
            String fecha = dateFormat.format(cargo.getFechaCreacion().toDate());
            tvDetailFecha.setText(fecha);
        } else {
            tvDetailFecha.setText("No disponible");
        }

        // ID
        if (cargo.getId() != null) {
            tvDetailId.setText(cargo.getId());
        }

        // 描述（放在最后）
        if (cargo.getDescripcion() != null && !cargo.getDescripcion().isEmpty()) {
            tvDetailDescripcion.setText(cargo.getDescripcion());
        } else {
            tvDetailDescripcion.setText("No hay descripción disponible");
        }

        //编辑按钮
        btnEditCargo.setOnClickListener(v -> {
            detailDialog.dismiss();
            showEditCargoDialog(cargo);
        });


        // 关闭按钮
        btnCloseDetail.setOnClickListener(v -> detailDialog.dismiss());

        // 显示弹窗
        detailDialog.show();
    }

    private void showEditCargoDialog(Cargo cargo) {
        final Dialog editDialog = new Dialog(context);
        editDialog.setContentView(R.layout.dialog_edit_cargo);
        editDialog.setCancelable(true);

        // 你需要在 dialog_edit_cargo.xml 里有这些 id
        com.google.android.material.textfield.TextInputEditText etTipo =
                editDialog.findViewById(R.id.etTipo);
        com.google.android.material.textfield.TextInputEditText etDescripcion =
                editDialog.findViewById(R.id.etDescripcion);
        com.google.android.material.textfield.TextInputEditText etImagenUrl =
                editDialog.findViewById(R.id.etImagenUrl);
        com.google.android.material.textfield.TextInputEditText etPesoTotal =
                editDialog.findViewById(R.id.etPesoTotal);
        com.google.android.material.switchmaterial.SwitchMaterial swEnviado =
                editDialog.findViewById(R.id.swEnviado);
        swEnviado.setChecked(cargo.isEnviado());



        TextView tvError = editDialog.findViewById(R.id.tvError);
        Button btnCancel = editDialog.findViewById(R.id.btnCancel);
        Button btnSave = editDialog.findViewById(R.id.btnSave);

        // 预填充原数据
        etTipo.setText(cargo.getTipo() == null ? "" : cargo.getTipo());
        etDescripcion.setText(cargo.getDescripcion() == null ? "" : cargo.getDescripcion());
        etImagenUrl.setText(cargo.getImagenUrl() == null ? "" : cargo.getImagenUrl());
        etPesoTotal.setText(String.valueOf(cargo.getPesoTotal()));
        swEnviado.setChecked(cargo.isEnviado());

        btnCancel.setOnClickListener(v -> editDialog.dismiss());





        //让它占屏幕90%
        Window window = editDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = (int) (getScreenWidth() * 0.90); // ✅ 90% 屏宽
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);

        }


        btnSave.setOnClickListener(v -> {
            String tipo = etTipo.getText() == null ? "" : etTipo.getText().toString().trim();
            String descripcion = etDescripcion.getText() == null ? "" : etDescripcion.getText().toString().trim();
            String imagenUrl = etImagenUrl.getText() == null ? "" : etImagenUrl.getText().toString().trim();
            String pesoStr = etPesoTotal.getText() == null ? "" : etPesoTotal.getText().toString().trim();

            if (tipo.isEmpty()) {
                tvError.setText("El tipo es obligatorio");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            double pesoTotal = 0.0;
            try {
                if (!pesoStr.isEmpty()) pesoTotal = Double.parseDouble(pesoStr);
            } catch (Exception e) {
                tvError.setText("El peso debe ser un número válido");
                tvError.setVisibility(View.VISIBLE);
                return;
            }

            tvError.setVisibility(View.GONE);
            btnSave.setEnabled(false);

            java.util.Map<String, Object> updates = new java.util.HashMap<>();
            updates.put("tipo", tipo);
            updates.put("descripcion", descripcion);
            updates.put("imagenUrl", imagenUrl);
            updates.put("pesoTotal", pesoTotal);
            boolean enviado = swEnviado.isChecked();
            updates.put("enviado", enviado);


            com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("cargos")
                    .document(cargo.getId())
                    .update(updates) // ✅ 必须用 update，不要 set
                    .addOnSuccessListener(aVoid -> {
                        android.widget.Toast.makeText(context, "Actualizado", android.widget.Toast.LENGTH_SHORT).show();
                        editDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("CargoAdapter", "update failed", e);
                        tvError.setText("Error: " + e.getMessage());
                        tvError.setVisibility(View.VISIBLE);
                        btnSave.setEnabled(true);
                    });
        });

        editDialog.show();
    }


    // 添加获取屏幕宽度的方法
    private int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (context instanceof Activity) {
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        } else {
            // 备用方法
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics.widthPixels;
    }

    @Override
    public int getItemCount() {
        return cargoList.size();
    }

    static class CargoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCargo;
        TextView tvTipo, tvPeso, tvEstado,tvFecha;
        TextView tvSecurity;


        public CargoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCargo = itemView.findViewById(R.id.imgCargo);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            tvPeso = itemView.findViewById(R.id.tvPeso);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvFecha = itemView.findViewById(R.id.tvFecha); // 初始化 tvFecha
            tvSecurity = itemView.findViewById(R.id.tvSecurity);

        }
    }
}