const invalidVatCodes = [
  '011111111',
  '022222222',
  '033333333',
  '044444444',
  '055555555',
  '066666666',
  '077777777',
  '088888888',
  '099999999',
  '000000000',
  '111111111',
  '222222222',
  '333333333',
  '444444444',
  '555555555',
  '666666666',
  '777777777',
  '888888888',
  '999999999'
];

export function vatValidator(value: string): boolean {
  if (!value) {
    return true;
  }

  // Check for 9 numeric digits
  if (!value.match(/^[0-9]{9}$/)) {
    return false;
  }
  // Check for invalid values
  if (invalidVatCodes.includes(value)) {
    return false;
  }

  // Check value
  let sum = 0, remainder: number;

  for (let i = 0; i < 8; i++) {
    sum = sum + Number(value.charAt(i)) * Math.pow(2, 8 - i);
  }
  remainder = sum % 11;

  // Check digits
  if (((remainder === 10) && (Number(value.charAt(8)) !== 0)) ||
    ((remainder < 10) && (remainder !== Number(value.charAt(8))))) {
    return false;
  }

  return true;
}

