package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.models.Report;

/**
 * Created by Henrique on 14/10/2017.
 */

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsViewHolder>{
    private List<Report> reports;
    private Context context;
    private ReportOnClickListener reportOnClickListener;

    public ReportsAdapter(List<Report> reports, Context context, ReportOnClickListener reportOnClickListener) {
        this.reports = reports;
        this.context = context;
        this.reportOnClickListener = reportOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.reports != null ? this.reports.size() : 0;
    }

    @Override
    public ReportsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_reports, viewGroup, false);
        ReportsViewHolder holder = new ReportsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ReportsViewHolder holder, final int position) {
        Report report = reports.get(position);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date resultdate = new Date(report.timestamp);
        String date = sdf.format(resultdate);

        holder.reportTimestamp.setText(date);

        if (reportOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportOnClickListener.onClickReport(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addReport(Report report) {
        reports.add(report);
    }

    public void removeReport(int position) {
        reports.remove(position);
    }

    public List<Report> getReports() {
        return reports;
    }

    public interface ReportOnClickListener {
        void onClickReport(View view, int idx);
    }

    public static class ReportsViewHolder extends RecyclerView.ViewHolder {
        public TextView reportTimestamp;

        public ReportsViewHolder(View view) {
            super(view);
            reportTimestamp = (TextView) view.findViewById(R.id.reportTimestamp);
        }
    }
}
