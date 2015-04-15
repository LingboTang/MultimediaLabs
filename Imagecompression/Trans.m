Transmiting_Lst = [160 160 149 180 175 175 175 175;
                   150 151 153 156 159 156 156 156;
                   150 155 160 163 158 160 160 160;
                   179 161 162 160 160 159 159 159;
                   179 180 161 162 128 128 128 128;
                   150 150 190 190 160 177 177 177;
                   150 150 190 190 162 157 157 157;
                   162 162 161 161 163 188 188 188;]

Transmiting4_4 = zeros(4,4);
for i = 1:2:8
    for j = 1:2:8
        Transmiting4_4(i-(i-1)/2,j-(j-1)/2) = (Transmiting_Lst(i,j)+Transmiting_Lst(i+1,j)+Transmiting_Lst(i,j+1)+Transmiting_Lst(i+1,j+1))/4;
    end
end
Transmiting4_4 = Transmiting4_4
BB = int16(Transmiting4_4)   
Transmiting2_2 = zeros(2,2);
for i = 1:2:4
    for j = 1:2:4
        Transmiting2_2(i-(i-1)/2,j-(j-1)/2) = (Transmiting4_4(i,j)+Transmiting4_4(i+1,j)+Transmiting4_4(i,j+1)+Transmiting4_4(i+1,j+1))/4;
    end
end
Transmiting2_2=Transmiting2_2
CC =int16(Transmiting2_2)
Transmiting1 = mean2(Transmiting2_2)
DD = int16(Transmiting1)

different8_8 = zeros(8,8);
for i = 1:4
    for j = 1:4
        different8_8(i+(i-1),j+(j-1)) = Transmiting_Lst(i+(i-1),j+(j-1))-Transmiting4_4(i,j);
        different8_8(i+(i-1)+1,j+(j-1)) = Transmiting_Lst(i+(i-1)+1,j+(j-1))-Transmiting4_4(i,j);
        different8_8(i+(i-1),j+(j-1)+1) = Transmiting_Lst(i+(i-1),j+(j-1)+1)-Transmiting4_4(i,j);
        different8_8(i+(i-1)+1,j+(j-1)+1) = Transmiting_Lst(i+(i-1)+1,j+(j-1)+1)-Transmiting4_4(i,j);
    end
end
different8_8 =different8_8
int16(different8_8)


different4_4 = zeros(4,4);
for i = 1:2
    for j = 1:2
        different4_4(i+(i-1),j+(j-1)) = Transmiting4_4(i+(i-1),j+(j-1))-Transmiting2_2(i,j);
        different4_4(i+(i-1)+1,j+(j-1)) = Transmiting4_4(i+(i-1)+1,j+(j-1))-Transmiting2_2(i,j);
        different4_4(i+(i-1),j+(j-1)+1) = Transmiting4_4(i+(i-1),j+(j-1)+1)-Transmiting2_2(i,j);
        different4_4(i+(i-1)+1,j+(j-1)+1) = Transmiting4_4(i+(i-1)+1,j+(j-1)+1)-Transmiting2_2(i,j);
    end
end
different4_4 =different4_4
int16(different4_4)

different2_2 = zeros(2,2);

i = 1;
j = 1;
different2_2(i+(i-1),j+(j-1)) = Transmiting2_2(i+(i-1),j+(j-1))-Transmiting1;
different2_2(i+(i-1)+1,j+(j-1)) = Transmiting2_2(i+(i-1)+1,j+(j-1))-Transmiting1;
different2_2(i+(i-1),j+(j-1)+1) = Transmiting2_2(i+(i-1),j+(j-1)+1)-Transmiting1;
different2_2(i+(i-1)+1,j+(j-1)+1) = Transmiting2_2(i+(i-1)+1,j+(j-1)+1)-Transmiting1;

different2_2 =different2_2
int16(different2_2)

new8_8 = different8_8/2
new8_8 = int8(new8_8)*2
% int8(new8_8)
new4_4 = different4_4/2
new4_4 = int8(new4_4)*2
% int8(new4_4)
new2_2 = different2_2/2
new2_2 = int8(new2_2)*2
% int8(new2_2)