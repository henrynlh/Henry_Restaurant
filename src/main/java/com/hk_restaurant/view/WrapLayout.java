package com.hk_restaurant.view;

import javax.swing.*;
import java.awt.*;

/**
 * Layout tùy biến kế thừa từ FlowLayout.
 * Mục đích của layout này là tự động xuống dòng khi số lượng component vượt quá chiều rộng hiển thị.
 */
public class WrapLayout extends FlowLayout {

    /**
     * Constructor mặc định.
     */
    public WrapLayout() {
        super();
    }

    /**
     * Constructor khởi tạo layout với kiểu canh lề.
     *
     * @param align kiểu canh lề
     */
    public WrapLayout(int align) {
        super(align);
    }

    /**
     * Constructor khởi tạo layout với kiểu canh lề, khoảng cách ngang và dọc.
     *
     * @param align kiểu canh lề
     * @param hgap khoảng cách ngang giữa các component
     * @param vgap khoảng cách dọc giữa các dòng
     */
    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /**
     * Tính kích thước ưu tiên của layout khi cần hiển thị component.
     *
     * @param target container cần tính kích thước
     * @return kích thước ưu tiên
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    /**
     * Tính kích thước tối thiểu của layout.
     *
     * @param target container cần tính kích thước
     * @return kích thước tối thiểu
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension minimum = layoutSize(target, false);
        minimum.width -= (getHgap() + 1);
        return minimum;
    }

    /**
     * Tính toán kích thước tổng thể của layout có hỗ trợ tự động xuống dòng.
     *
     * @param target container cần tính
     * @param preferred xác định lấy preferred size hay minimum size của component con
     * @return kích thước phù hợp của layout
     */
    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;

            Container container = target;
            while (container.getSize().width == 0 && container.getParent() != null) {
                container = container.getParent();
            }

            targetWidth = container.getSize().width;

            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();

            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }

                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }

                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }

            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null) {
                dim.width -= (hgap + 1);
            }

            return dim;
        }
    }

    /**
     * Cộng dồn kích thước của một dòng component vào kích thước tổng thể.
     *
     * @param dim kích thước tổng hiện tại
     * @param rowWidth chiều rộng của dòng
     * @param rowHeight chiều cao của dòng
     */
    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);

        if (dim.height > 0) {
            dim.height += getVgap();
        }

        dim.height += rowHeight;
    }
}