/* Hello World program */

#include <stdio.h>

int main() {
	int i = 0;

	switch(i){
		case 1:
			printf("UNO\n");
		break;
		
		case 2:
			printf("DOS\n");
		break;
		
		default:
			i++;
		break;
	}
	while(i>=0){
		printf("WHILE FUNCTION\n");
		i--;
	}

	do{
		printf("DO FUNCTION %d \n",i);
		if(true) i++;
		i++;
	} while(i<10);
}
